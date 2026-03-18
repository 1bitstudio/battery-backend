package ru.battery.main.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.battery.main.exception.ConflictException;
import ru.battery.main.exception.NotFoundException;
import ru.battery.main.exception.ValidationException;
import ru.battery.main.users.dto.CreateUserDto;
import ru.battery.main.users.dto.UpdateUserDto;
import ru.battery.main.users.dto.UserDto;
import ru.battery.main.users.dto.UserMapper;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserDto createUser(CreateUserDto createUserDto) {
        if (userStorage.findUserByEmail(createUserDto.getEmail()) != null) {
            throw new ConflictException("Email адрес уже используется!");
        }
        User user = UserMapper.toUserFromCreateDto(createUserDto);
        String hashedPassword = passwordEncoder.encode(createUserDto.getPassword());
        user.setPassword(hashedPassword);
        User createdUser = userStorage.save(user);

        String verifyCode = generatedVerifyCode();
        //save to keydb
        String subject = "Подтверждение электронной почты";
        String message = String.format(
                "Здравствуйте!\n\n" +
                        "Вы запрашивали код для подтверждения электронной почты.\n\n" +
                        "Ваш код: %s\n\n" +
                        "Пожалуйста, введите этот код в течение 5 минут, чтобы завершить подтверждение.\n\n" +
                        "Если вы не запрашивали этот код — просто проигнорируйте это письмо.\n\n" +
                        "С уважением,\n" +
                        "Команда поддержки \"Ыт Студии\"",
                verifyCode
        );
        emailService.sendSimpleMessage(user.getEmail(), subject, message);

        return UserMapper.toUserDto(createdUser);
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
        if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().equals(user.getEmail())) {
            user.setEmail(updateUserDto.getEmail());
        }

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
}
