package ru.battery.main.responserul;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PredictionResponseRulStorage extends JpaRepository<PredictionResponseRul, Long> {
    Optional<PredictionResponseRul> findByRequestId(Long requestId);
}
