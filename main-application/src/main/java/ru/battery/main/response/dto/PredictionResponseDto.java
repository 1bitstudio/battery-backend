package ru.battery.main.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponseDto {
    private Long requestId;

    private String status;

    private PredictionResultDto result;

    private String error;
}
