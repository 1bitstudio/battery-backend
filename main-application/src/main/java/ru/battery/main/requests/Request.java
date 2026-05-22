package ru.battery.main.requests;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.battery.main.users.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "request_name")
    private String requestName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "model_type")
    @Enumerated(EnumType.STRING)
    private ModelTypes modelType;
}
