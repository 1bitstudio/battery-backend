package ru.battery.main.response;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.battery.main.response.dto.PredictionResponseTotalDto;

@RestController
@RequestMapping("/prediction-response")
@RequiredArgsConstructor
public class PredictionResponseController {
    private final PredictionResponseService predictionResponseService;

    @GetMapping("/{userId}/{requestId}")
    public PredictionResponseTotalDto getPredictionResponse(@PathVariable Long userId, @PathVariable Long requestId) {
        return predictionResponseService.getPredictionResponse(userId, requestId);
    }
}
