package ru.battery.main.users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStorage extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
}
