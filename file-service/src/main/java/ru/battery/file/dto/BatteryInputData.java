package ru.battery.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatteryInputData {
    private Double nominalCapacityInAh;

    private List<CycleData> cycleData;

    private Integer obsCycles;
}
