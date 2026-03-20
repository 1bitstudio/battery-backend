package ru.battery.main.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.battery.main.users.dto.CreateUserDto;
import ru.battery.main.users.dto.UpdateUserDto;
import ru.battery.main.users.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@RequestBody @Valid CreateUserDto createUserDto) {
        return userService.createUser(createUserDto);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PatchMapping("/update/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserDto updateUserDto) {
        return userService.updateUser(userId, updateUserDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PatchMapping("/verify/{userId}")
    public UserDto verifyUser(@PathVariable Long userId, @RequestBody String verifyCode) {
        return userService.verifyUser(userId, verifyCode);
    }
}
