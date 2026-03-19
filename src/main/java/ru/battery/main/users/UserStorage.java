package ru.battery.main.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStorage extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
}
