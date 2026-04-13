package ru.battery.main.requests.dto;

import ru.battery.main.requests.Request;

public class RequestMapper {
    public static RequestDto toDto(Request request) {
        RequestDto dto = new RequestDto();
        dto.setRequestId(request.getId());
        dto.setRequestName(request.getRequestName());
        dto.setCreatedAt(request.getCreatedAt());
        return dto;
    }
}
