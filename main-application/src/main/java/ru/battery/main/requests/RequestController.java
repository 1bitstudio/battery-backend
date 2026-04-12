package ru.battery.main.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.battery.main.requests.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/{userId}")
    public List<RequestDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }

    @DeleteMapping("/{requestId}")
    public void deleteRequest(@PathVariable Long requestId) {
        requestService.deleteByRequestId(requestId);
    }
}
