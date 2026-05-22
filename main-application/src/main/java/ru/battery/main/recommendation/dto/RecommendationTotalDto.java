package ru.battery.main.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationTotalDto {
    private Long requestId;

    private String status;

    private String message;

    private Double nominalVoltageInV;

    private RecommendationProfileResult conservative;

    private RecommendationProfileResult balanced;

    private RecommendationProfileResult fast;

    private String error;
}
