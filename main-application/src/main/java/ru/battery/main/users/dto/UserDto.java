package ru.battery.main.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.battery.main.users.AccountType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private AccountType accountType;
}
