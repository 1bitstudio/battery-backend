package ru.battery.main.responsesoh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.battery.main.requests.Request;

@Entity
@Table(name = "prediction_response_soh")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponseSoh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_soh_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    private String status;

    private String error;

    @Column(name = "predicted_soh")
    private Double predictedSoh;

    @Column(name = "target_cycle")
    private Integer targetCycle;
}
