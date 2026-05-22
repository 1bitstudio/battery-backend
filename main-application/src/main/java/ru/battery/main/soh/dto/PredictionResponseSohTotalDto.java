package ru.battery.main.soh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponseSohTotalDto {
    private Long requestId;

    private Double predictedSoh;

    private Integer targetCycle;
}
