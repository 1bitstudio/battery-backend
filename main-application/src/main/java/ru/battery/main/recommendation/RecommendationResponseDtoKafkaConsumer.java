package ru.battery.main.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.recommendation.dto.RecommendationProfileResult;
import ru.battery.main.recommendation.dto.RecommendationResponseDto;
import ru.battery.main.recommendation.dto.RecommendationResponseMapper;
import ru.battery.main.requests.Request;
import ru.battery.main.requests.RequestStorage;

@Service
@RequiredArgsConstructor
public class RecommendationResponseDtoKafkaConsumer {
    private final RequestStorage requestStorage;
    private final RecommendationResponseStorage recommendationResponseStorage;
    private final RecommendationProfileStorage recommendationProfileStorage;


    @KafkaListener(topics = "recommend_responses", groupId = "soh-ml-worker",
            containerFactory = "mlRecommendationContainerFactory")
    public void consumeRecommendationResponseDto(RecommendationResponseDto recommendationResponseDto) {
        Request request = requestStorage.findById(recommendationResponseDto.getRequestId()).orElseThrow(() ->
                new NotFoundException("Запрос с ID: " + recommendationResponseDto.getRequestId() + " не найден."));

        RecommendationResponse recommendationResponse = RecommendationResponseMapper
                .toRecommendationResponseFromRecommendationResponseDto(recommendationResponseDto);
        recommendationResponse.setRequest(request);
        RecommendationResponse recommendationResponseTotal = recommendationResponseStorage.save(recommendationResponse);

        if (recommendationResponseTotal.getError() == null) {
            saveRecommendationProfileData(recommendationResponseTotal, recommendationResponseDto);
        }
    }

    private void saveRecommendationProfileData(RecommendationResponse recommendationResponseTotal,
                                               RecommendationResponseDto recommendationResponseDto) {
        RecommendationProfileResult conservative = recommendationResponseDto.getResult().getConservative();
        RecommendationProfile recommendationProfileConservative = RecommendationResponseMapper
                .toRecommendationProfileFromRecommendationResponseDto(recommendationResponseTotal, conservative);
        recommendationProfileConservative.setProfileType("conservative");
        recommendationProfileStorage.save(recommendationProfileConservative);

        RecommendationProfileResult balanced = recommendationResponseDto.getResult().getBalanced();
        RecommendationProfile recommendationProfileBalanced = RecommendationResponseMapper
                .toRecommendationProfileFromRecommendationResponseDto(recommendationResponseTotal, balanced);
        recommendationProfileBalanced.setProfileType("balanced");
        recommendationProfileStorage.save(recommendationProfileBalanced);

        RecommendationProfileResult fast = recommendationResponseDto.getResult().getFast();
        RecommendationProfile recommendationProfileFast = RecommendationResponseMapper
                .toRecommendationProfileFromRecommendationResponseDto(recommendationResponseTotal, fast);
        recommendationProfileFast.setProfileType("fast");
        recommendationProfileStorage.save(recommendationProfileFast);
    }
}
