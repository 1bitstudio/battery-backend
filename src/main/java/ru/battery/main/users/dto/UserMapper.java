package ru.battery.main.users.dto;

import ru.battery.main.users.AccountType;
import ru.battery.main.users.User;

import java.time.LocalDateTime;

public class UserMapper {
    public static User toUserFromCreateDto(CreateUserDto createUserDto) {
        User user = new User();
        user.setCreatedAt(LocalDateTime.now());
        user.setEmail(createUserDto.getEmail());
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setAccountType(AccountType.NOT_CONFIGURED);
        return user;
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setAccountType(user.getAccountType());
        return userDto;
    }
}
