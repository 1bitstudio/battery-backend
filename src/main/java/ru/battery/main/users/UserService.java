package ru.battery.main.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.ConflictException;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.exceptions.ValidationException;
import ru.battery.main.security.dto.JwtAuthenticationDto;
import ru.battery.main.security.dto.RefreshTokenDto;
import ru.battery.main.security.dto.UserCredentialsDto;
import ru.battery.main.security.jwt.JwtService;
import ru.battery.main.users.dto.CreateUserDto;
import ru.battery.main.users.dto.UpdateUserDto;
import ru.battery.main.users.dto.UserDto;
import ru.battery.main.users.dto.UserMapper;

import javax.naming.AuthenticationException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    public UserDto createUser(CreateUserDto createUserDto) {
        if (userStorage.findUserByEmail(createUserDto.getEmail()).isPresent()) {
            throw new ConflictException("Email адрес уже используется!");
        }
        User user = UserMapper.toUserFromCreateDto(createUserDto);
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        User createdUser = userStorage.save(user);

        String verifyCode = generatedVerifyCode();
        saveToKeyDb("user:" + user.getEmail(), verifyCode);
        String subject = "Подтверждение электронной почты";
        String message = String.format(
                "Здравствуйте!\n\n" +
                        "Вы запрашивали код для подтверждения электронной почты.\n\n" +
                        "Ваш код: %s\n\n" +
                        "Пожалуйста, введите этот код в течение 5 минут, чтобы завершить подтверждение.\n\n" +
                        "Если вы не запрашивали этот код — просто проигнорируйте это письмо.\n\n" +
                        "С уважением,\n" +
                        "Команда поддержки \"ЫТ Студии\"",
                verifyCode
        );
        emailService.sendSimpleMessage(user.getEmail(), subject, message);

        return UserMapper.toUserDto(createdUser);
    }

    public JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        User user = findByCredentials(userCredentialsDto);
        return jwtService.generateAuthToken(user.getEmail(), user.getId());
    }

    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    public List<UserDto> getUsers() {
        return userStorage.findAll().stream().map(UserMapper::toUserDto).toList();
    }

    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователя с ID: " + userId + " не существует!")));
    }

    public UserDto updateUser(Long userId, UpdateUserDto updateUserDto) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователя с ID: " + userId + " не существует!"));

        if (updateUserDto.getFirstName() != null && !updateUserDto.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(updateUserDto.getFirstName());
        }

        if (updateUserDto.getLastName() != null && !updateUserDto.getLastName().equals(user.getLastName())) {
            user.setLastName(updateUserDto.getLastName());
        }

        if (updateUserDto.getPassword() != null) {
            String hashedPassword = passwordEncoder.encode(updateUserDto.getPassword());
            if (hashedPassword.equals(user.getPassword())) {
                throw new ValidationException("Новый пароль совпадает с текущим");
            }
            String newHashedPassword = passwordEncoder.encode(updateUserDto.getPassword());
            user.setPassword(newHashedPassword);
        }

        User updatedUser = userStorage.save(user);
        return UserMapper.toUserDto(updatedUser);
    }

    public void deleteUser(Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователя с ID: " + userId + " не существует!"));
        userStorage.delete(user);
    }

    private String generatedVerifyCode() {
        Random random = new  Random();
        StringBuilder totalCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            totalCode.append(random.nextInt(10));
        }
        return totalCode.toString();
    }

    private User findByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Optional<User> optionalUser = userStorage.findUserByEmail(userCredentialsDto.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(userCredentialsDto.getPassword(), user.getPassword())) {
                return user;
            }
        }
        throw new AuthenticationException("Неверный адрес электронной почты или пароль");
    }

    private User findByEmail(String email) {
        return userStorage.findUserByEmail(email).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с электронной почтой " + "%s не найден", email)));
    }

    private void saveToKeyDb(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(5));
    }

    public UserDto verifyUser(Long userId, String verifyCode) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователя с ID: " + userId + " не существует!"));
        String trueCode = redisTemplate.opsForValue().get("user:" + user.getEmail());
        if (trueCode != null && trueCode.equals(verifyCode)) {
            user.setAccountType(AccountType.CONFIGURED);
            return UserMapper.toUserDto(userStorage.save(user));
        }
        throw new ValidationException("Указан неправильный код подтверждения или срок его действия истек");
    }
}
