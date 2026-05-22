package ru.battery.main.rul.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponseRulDto {
    private Long requestId;

    private String status;

    private Double predictionRul;

    private String error;
}
