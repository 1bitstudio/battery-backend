package ru.battery.main.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponseDto {
    @JsonProperty("request_id")
    private Long requestId;

    private String status;

    private PredictionResultDto result;

    private String error;
}
