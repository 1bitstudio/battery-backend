package ru.battery.main.recommendation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.battery.main.requests.Request;

@Entity
@Table(name = "recommendation_response")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    private String status;

    private String message;

    @Column(name = "target_energy_wh")
    private Double targetEnergyWh;

    @Column(name = "nominal_voltage_in_v")
    private Double nominalVoltageInV;

    private String error;
}
