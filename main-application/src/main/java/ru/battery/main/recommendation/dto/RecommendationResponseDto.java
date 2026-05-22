package ru.battery.main.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponseDto {
    private Long requestId;

    private String status;

    private RecommendationResult result;

    private String error;
}
