package ru.battery.main.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MlRequestForRecommendation {
    private Long requestId;

    private Double nominalVoltageInV;

    private String modelType;

    private BatteryInputData batteryInputData;
}
