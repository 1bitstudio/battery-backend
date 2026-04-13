package ru.battery.main.response.dto;

import ru.battery.main.response.PredictionResponse;

public class PredictionResponseMapper {
    public static PredictionResponse toPredictionResponseFromPredictionResponseDto(PredictionResponseDto
                                                                                           predictionResponseDto) {
        PredictionResponse predictionResponse = new PredictionResponse();
        predictionResponse.setStatus(predictionResponseDto.getStatus());
        if (predictionResponseDto.getError() != null) {
            predictionResponse.setError(predictionResponseDto.getError());
        }
        if (predictionResponseDto.getResult() != null) {
            predictionResponse.setPredictedSoh(predictionResponseDto.getResult().getPredictedSoh());
            predictionResponse.setPredictedSohPercent(predictionResponseDto.getResult().getPredictedSohPercent());
            predictionResponse.setTargetCycle(predictionResponseDto.getResult().getTargetCycle());
        }
        return predictionResponse;
    }
}
