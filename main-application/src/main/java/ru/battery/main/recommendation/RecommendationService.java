package ru.battery.main.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.ForbiddenException;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.recommendation.dto.RecommendationResponseMapper;
import ru.battery.main.recommendation.dto.RecommendationTotalDto;
import ru.battery.main.requests.Request;
import ru.battery.main.requests.RequestStorage;
import ru.battery.main.users.User;
import ru.battery.main.users.UserStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final UserStorage userStorage;
    private final RequestStorage requestStorage;
    private final RecommendationResponseStorage recommendationResponseStorage;
    private final RecommendationProfileStorage recommendationProfileStorage;

    public RecommendationTotalDto getRecommendation(Long userId, Long requestId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID: " + userId + " не найден."));
        Request request = requestStorage.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с ID: " + requestId + " не найден."));
        if (!request.getUser().equals(user)) {
            throw new ForbiddenException("У пользователя с ID = " + userId + " нет доступа к запросу с ID = " +
                    requestId);
        }

        Optional<RecommendationResponse> recommendationResponseOptional = recommendationResponseStorage
                .findByRequestId(requestId);
        if (recommendationResponseOptional.isEmpty()) {
            throw new NotFoundException("Рекомендаций для запроса с ID = " + requestId + " не найдено.");
        }

        RecommendationResponse recommendationResponse = recommendationResponseOptional.get();
        if (recommendationResponse.getError() != null) {
            return new RecommendationTotalDto(requestId, recommendationResponse.getStatus(),
                    recommendationResponse.getMessage(), recommendationResponse.getNominalVoltageInV(), null,
                    null, null, recommendationResponse.getError());
        }

        RecommendationProfile recommendationProfileConservative = getRecommendationProfile(
                recommendationResponse.getId(), "conservative");
        RecommendationProfile recommendationProfileBalanced = getRecommendationProfile(
                recommendationResponse.getId(), "balanced");
        RecommendationProfile recommendationProfileFast = getRecommendationProfile(
                recommendationResponse.getId(), "fast");

        return new RecommendationTotalDto(requestId, recommendationResponse.getStatus(),
                recommendationResponse.getMessage(), recommendationResponse.getNominalVoltageInV(),
                RecommendationResponseMapper.toRecommendationProfileResultFromRecommendationProfile(
                        recommendationProfileConservative), RecommendationResponseMapper
                .toRecommendationProfileResultFromRecommendationProfile(recommendationProfileBalanced),
                RecommendationResponseMapper.toRecommendationProfileResultFromRecommendationProfile(
                        recommendationProfileFast), recommendationResponse.getError());
    }

    private RecommendationProfile getRecommendationProfile(Long recommendationId, String profileType) {
        Optional<RecommendationProfile> recommendationProfileOptional =
                recommendationProfileStorage.findByRecommendationResponseIdAndProfileType(recommendationId,
                        profileType);
        if (recommendationProfileOptional.isEmpty()) {
            throw new NotFoundException("Не удалось найти данные для рекомндаций с ID = " + recommendationId);
        }

        return recommendationProfileOptional.get();
    }
}
