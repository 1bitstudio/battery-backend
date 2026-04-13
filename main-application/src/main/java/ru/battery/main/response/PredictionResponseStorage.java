package ru.battery.main.response;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PredictionResponseStorage extends JpaRepository<PredictionResponse, Long> {
    Optional<PredictionResponse> findByRequestId(Long requestId);
}
