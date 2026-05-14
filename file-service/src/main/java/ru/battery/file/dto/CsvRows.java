package ru.battery.file.dto;

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
    private Double timeInS;
    private Double temperatureInC;
    private Double internalResistanceInOhm;
    private String formFactor;
    private String anodeComposition;
    private String cathodeComposition;
}
