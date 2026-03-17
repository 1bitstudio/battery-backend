package ru.battery.main.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.battery.main.exception.NotFoundException;
import ru.battery.main.exception.ValidationException;
import ru.battery.main.users.dto.CreateUserDto;
import ru.battery.main.users.dto.UpdateUserDto;
import ru.battery.main.users.dto.UserDto;
import ru.battery.main.users.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto createUser(CreateUserDto createUserDto) {
        if (userStorage.findUserByEmail(createUserDto.getEmail()) != null) {
            throw new ValidationException("Email адрес уже используется!");
        }
        User user = UserMapper.toUserFromCreateDto(createUserDto);
        User createdUser = userStorage.save(user);
        return UserMapper.toUserDto(createdUser);
    }

    public List<UserDto> getUsers() {
        return userStorage.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
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

        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().equals(user.getPassword())) {
            user.setPassword(updateUserDto.getPassword());
        }

        User updatedUser = userStorage.save(user);
        return UserMapper.toUserDto(updatedUser);
    }

    public void deleteUser(Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователя с ID: " + userId + " не существует!"));
        userStorage.delete(user);
    }
}
