package ru.battery.main.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MlRequest {
    private Long requestId;

    private BatteryInputData batteryInputData;
}
