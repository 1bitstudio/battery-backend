package ru.battery.main.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long requestId;

    private String requestName;

    private LocalDateTime createdAt;
}
