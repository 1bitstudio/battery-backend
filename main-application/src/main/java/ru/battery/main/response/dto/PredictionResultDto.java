package ru.battery.main.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResultDto {
    @JsonProperty("predicted_soh")
    private Double predictedSoh;

    @JsonProperty("predicted_soh_percent")
    private Double predictedSohPercent;

    @JsonProperty("target_cycle")
    private Integer targetCycle;
}
