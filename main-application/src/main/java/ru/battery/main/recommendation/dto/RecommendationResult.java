package ru.battery.main.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResult {
    private String message;

    private Double targetEnergyWh;

    private Double nominalVoltageInV;

    private RecommendationProfileResult conservative;

    private RecommendationProfileResult balanced;

    private RecommendationProfileResult fast;
}
