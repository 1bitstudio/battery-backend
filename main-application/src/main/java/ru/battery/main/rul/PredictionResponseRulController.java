package ru.battery.main.rul;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.battery.main.rul.dto.PredictionResponseRulTotalDto;

@RestController
@RequestMapping("/prediction-response-rul")
@RequiredArgsConstructor
public class PredictionResponseRulController {
    private final PredictionResponseRulService predictionResponseRulService;

    @GetMapping("/{userId}/{requestId}")
    public PredictionResponseRulTotalDto getPredictionResponse(@PathVariable Long userId, @PathVariable Long requestId) {
        return predictionResponseRulService.getPredictionResponse(userId, requestId);
    }
}
