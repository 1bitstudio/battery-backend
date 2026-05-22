package ru.battery.main.soh;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.battery.main.soh.dto.PredictionResponseSohTotalDto;

import java.util.List;

@RestController
@RequestMapping("/prediction-response-soh")
@RequiredArgsConstructor
public class PredictionResponseSohController {
    private final PredictionResponseSohService predictionResponseSohService;

    @GetMapping("/{userId}/{requestId}")
    public List<PredictionResponseSohTotalDto> getPredictionResponseSoh(@PathVariable Long userId,
                                                                        @PathVariable Long requestId) {
        return predictionResponseSohService.getPredictionResponseSoh(userId, requestId);
    }
}
