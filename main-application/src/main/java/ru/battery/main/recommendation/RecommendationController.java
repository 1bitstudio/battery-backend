package ru.battery.main.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.battery.main.recommendation.dto.RecommendationTotalDto;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/{userId}/{requestId}")
    public RecommendationTotalDto getRecommendation(@PathVariable Long userId, @PathVariable Long requestId) {
        return recommendationService.getRecommendation(userId, requestId);
    }
}
