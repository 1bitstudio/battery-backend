package ru.battery.main.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatteryInputData {
    private Double nominalCapacityInAh;

    private String formFactor;

    private List<CycleData> cycleData;

    private Integer obsCycles;
}
