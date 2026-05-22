package ru.battery.main.rul.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponseRulTotalDto {
    private Long requestId;

    private Double predictedRul;
}
