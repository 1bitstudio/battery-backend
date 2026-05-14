package ru.battery.main.responsesoh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponseSohDto {
    private Long requestId;

    private String status;

    private PredictionResultSohDto result;

    private String error;
}
