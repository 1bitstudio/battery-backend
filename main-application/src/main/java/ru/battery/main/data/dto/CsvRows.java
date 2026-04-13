package ru.battery.main.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsvRows {
    private Integer cycleNumber;
    private Integer pointIndex;
    private Double voltageInV;
    private Double currentInA;
    private Double chargeCapacityInAh;
    private Double dischargeCapacityInAh;
    private Double nominalCapacityInAh;
    private Double socStart;
    private Double socEnd;
}
