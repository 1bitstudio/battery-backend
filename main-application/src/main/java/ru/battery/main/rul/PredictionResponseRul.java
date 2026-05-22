package ru.battery.main.rul;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.battery.main.requests.Request;

@Entity
@Table(name = "prediction_response_rul")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponseRul {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_rul_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    private String status;

    private String error;

    @Column(name = "predicted_rul")
    private Double predictedRul;
}
