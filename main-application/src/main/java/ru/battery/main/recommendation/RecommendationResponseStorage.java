package ru.battery.main.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationResponseStorage extends JpaRepository<RecommendationResponse, Long> {
    Optional<RecommendationResponse> findByRequestId(Long requestId);
}
