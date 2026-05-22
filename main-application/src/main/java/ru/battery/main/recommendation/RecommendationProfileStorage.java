package ru.battery.main.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationProfileStorage extends JpaRepository<RecommendationProfile, Long> {
    Optional<RecommendationProfile> findByRecommendationResponseIdAndProfileType(Long recommendationId,
                                                                                 String profileType);
}
