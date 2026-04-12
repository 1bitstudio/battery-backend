package ru.battery.main.response.dto;

import ru.battery.main.response.PredictionResponse;

public class PredictionResponseMapper {
    public static PredictionResponse toPredictionResponseFromPredictionResponseDto(PredictionResponseDto
                                                                                           predictionResponseDto) {
        PredictionResponse predictionResponse = new PredictionResponse();
        predictionResponse.setPredictedSoh(predictionResponseDto.getResult().getPredictedSoh());
        predictionResponse.setPredictedSohPercent(predictionResponseDto.getResult().getPredictedSohPercent());
        predictionResponse.setStatus(predictionResponseDto.getStatus());
        predictionResponse.setTargetCycle(predictionResponseDto.getResult().getTargetCycle());
        predictionResponse.setError(predictionResponseDto.getError());
        return predictionResponse;
    }
}
