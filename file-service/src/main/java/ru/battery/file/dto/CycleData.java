package ru.battery.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CycleData {
    private List<Double> voltageInV;

    private List<Double> currentInA;

    private List<Double> chargeCapacityInAh;

    private List<Double> dischargeCapacityInAh;

    private List<Double> timeInS;

    private List<Double> temperatureInC;
}
