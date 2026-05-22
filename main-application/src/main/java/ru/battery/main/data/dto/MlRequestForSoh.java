package ru.battery.main.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MlRequestForSoh {
    private Long requestId;

    private List<Integer> targetCycles;

    private String modelType;

    private BatteryInputData batteryInputData;
}
