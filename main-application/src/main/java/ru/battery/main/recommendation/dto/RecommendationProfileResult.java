package ru.battery.main.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationProfileResult {
    private Double chargeCRate;

    private Double dischargeCRate;

    private Double socMin;

    private Double socMax;

    private Double dod;

    private Double ambientTempProxy;

    private Double nominalVoltageV;

    private Double predictedDegradationRate;

    private Double chargeTimeSec;

    private Double deliveredEnergyWh;

    private Double thermalRisk;

    private Double predictionRul;
}
