package ru.battery.main.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.battery.main.requests.Request;

@Entity
@Table(name = "prediction_response")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    private String status;

    private String error;

    @Column(name = "predicted_soh")
    private Double predictedSoh;

    @Column(name = "predicted_soh_percent")
    private Double predictedSohPercent;

    @Column(name = "target_cycle")
    private Integer targetCycle;
}
