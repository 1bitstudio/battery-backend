package ru.battery.main.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResultDto {
    private Double predictedSoh;

    private Double predictedSohPercent;

    private Integer targetCycle;
}
