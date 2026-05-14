package ru.battery.main.responsesoh;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionResponseSohStorage extends JpaRepository<PredictionResponseSoh, Long> {
    List<PredictionResponseSoh> findAllByRequestId(Long requestId);
}
