package ru.battery.main.requests;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestStorage extends JpaRepository<Request, Long> {
    List<Request> findAllByUserId(Long userId);
}
