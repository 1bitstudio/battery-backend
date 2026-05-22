package ru.battery.main.recommendation.dto;

import ru.battery.main.recommendation.RecommendationProfile;
import ru.battery.main.recommendation.RecommendationResponse;

public class RecommendationResponseMapper {
    public static RecommendationResponse toRecommendationResponseFromRecommendationResponseDto(
            RecommendationResponseDto recommendationResponseDto) {
        RecommendationResponse recommendationResponse = new RecommendationResponse();
        recommendationResponse.setStatus(recommendationResponseDto.getStatus());
        recommendationResponse.setMessage(recommendationResponseDto.getResult().getMessage());
        recommendationResponse.setTargetEnergyWh(recommendationResponseDto.getResult().getTargetEnergyWh());
        recommendationResponse.setNominalVoltageInV(recommendationResponseDto.getResult().getNominalVoltageInV());
        recommendationResponse.setError(recommendationResponseDto.getError());
        return recommendationResponse;
    }

    public static RecommendationProfile toRecommendationProfileFromRecommendationResponseDto(
            RecommendationResponse recommendationResponse,
            RecommendationProfileResult recommendationProfileResult) {
        RecommendationProfile recommendationProfile = new RecommendationProfile();
        recommendationProfile.setRecommendationResponse(recommendationResponse);
        recommendationProfile.setChargeCRate(recommendationProfileResult.getChargeCRate());
        recommendationProfile.setDischargeCRate(recommendationProfileResult.getDischargeCRate());
        recommendationProfile.setSocMin(recommendationProfileResult.getSocMin());
        recommendationProfile.setSocMax(recommendationProfileResult.getSocMax());
        recommendationProfile.setDod(recommendationProfileResult.getDod());
        recommendationProfile.setAmbientTempProxy(recommendationProfileResult.getAmbientTempProxy());
        recommendationProfile.setNominalVoltageV(recommendationProfileResult.getNominalVoltageV());
        recommendationProfile.setPredictedDegradationRate(recommendationProfileResult.getPredictedDegradationRate());
        recommendationProfile.setChargeTimeSec(recommendationProfileResult.getChargeTimeSec());
        recommendationProfile.setDeliveredEnergyWh(recommendationProfileResult.getDeliveredEnergyWh());
        recommendationProfile.setThermalRisk(recommendationProfileResult.getThermalRisk());
        return recommendationProfile;
    }

    public static RecommendationProfileResult toRecommendationProfileResultFromRecommendationProfile(
            RecommendationProfile recommendationProfile) {
        RecommendationProfileResult recommendationProfileResult = new RecommendationProfileResult();
        recommendationProfileResult.setChargeCRate(recommendationProfile.getChargeCRate());
        recommendationProfileResult.setDischargeCRate(recommendationProfile.getDischargeCRate());
        recommendationProfileResult.setSocMin(recommendationProfile.getSocMin());
        recommendationProfileResult.setSocMax(recommendationProfile.getSocMax());
        recommendationProfileResult.setDod(recommendationProfile.getDod());
        recommendationProfileResult.setAmbientTempProxy(recommendationProfile.getAmbientTempProxy());
        recommendationProfileResult.setNominalVoltageV(recommendationProfile.getNominalVoltageV());
        recommendationProfileResult.setPredictedDegradationRate(recommendationProfile.getPredictedDegradationRate());
        recommendationProfileResult.setChargeTimeSec(recommendationProfile.getChargeTimeSec());
        recommendationProfileResult.setDeliveredEnergyWh(recommendationProfile.getDeliveredEnergyWh());
        recommendationProfileResult.setThermalRisk(recommendationProfile.getThermalRisk());
        return recommendationProfileResult;
    }
}
