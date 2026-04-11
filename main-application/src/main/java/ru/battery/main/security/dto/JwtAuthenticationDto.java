package ru.battery.main.security.dto;

import lombok.Data;

@Data
public class JwtAuthenticationDto {
    private String accessToken;
    private String refreshToken;
    private Long userId;
}
