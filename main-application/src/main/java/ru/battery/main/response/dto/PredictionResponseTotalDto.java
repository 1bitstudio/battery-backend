package ru.battery.main.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponseTotalDto {
    private Long requestId;

    private Double predictedSoh;

    private Double predictedSohPercent;

    private Integer targetCycle;
}
