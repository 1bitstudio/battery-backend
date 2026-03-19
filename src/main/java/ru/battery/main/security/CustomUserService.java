package ru.battery.main.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.battery.main.users.User;
import ru.battery.main.users.UserStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {
    private final UserStorage userStorage;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userStorage.findUserByEmail(username).map(CustomUserDetails::new).orElseThrow(() ->
                new UsernameNotFoundException(username));
    }
}
