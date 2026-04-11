package ru.battery.main.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {
    @NotBlank
    @Size(min = 2, max = 15, message = "Имя должно содержать от 2 до 15 символов")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 30, message = "Фамилия должна содержать от 2 до 30 символов")
    private String lastName;

    @NotBlank(message = "Email должен быть указан")
    @Size(min = 6, max = 254, message = "Email должен содержать от 6 до 254 символов")
    @Email(message = "Email должен содержать символ @")
    private String email;

    @NotBlank(message = "Пароль должен быть указан")
    @Size(min = 6, max = 30, message = "Пароль должен содержать от 6 до 30 символов")
    private String password;
}
