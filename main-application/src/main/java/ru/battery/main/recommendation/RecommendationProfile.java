package ru.battery.main.recommendation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommendation_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recommendation_id")
    private RecommendationResponse recommendationResponse;

    @Column(name = "profile_type")
    private String profileType;

    @Column(name = "charge_c_rate")
    private Double chargeCRate;

    @Column(name = "discharge_c_rate")
    private Double dischargeCRate;

    @Column(name = "soc_min")
    private Double socMin;

    @Column(name = "soc_max")
    private Double socMax;

    private Double dod;

    @Column(name = "ambient_temp_proxy")
    private Double ambientTempProxy;

    @Column(name = "nominal_voltage_v")
    private Double nominalVoltageV;

    @Column(name = "predicted_degradation_rate")
    private Double predictedDegradationRate;

    @Column(name = "charge_time_sec")
    private Double chargeTimeSec;

    @Column(name = "delivered_energy_wh")
    private Double deliveredEnergyWh;

    @Column(name = "thermal_risk")
    private Double thermalRisk;
}
