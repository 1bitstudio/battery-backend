package ru.battery.main.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatteryInputData {
    private Double nominalCapacityInAh;

    @JsonProperty("SOCInterval")
    private List<Double> SOCInterval;

    private List<CycleData> cycleData;

    private Integer obsCycles;
}
