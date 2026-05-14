package ru.battery.main.responserul.dto;

import ru.battery.main.responserul.PredictionResponseRul;

public class PredictionResponseRulMapper {
    public static PredictionResponseRul toPredictionResponseFromPredictionResponseDto(PredictionResponseRulDto
                                                                                              predictionResponseRulDto) {
        PredictionResponseRul predictionResponseRul = new PredictionResponseRul();
        predictionResponseRul.setStatus(predictionResponseRulDto.getStatus());
        if (predictionResponseRulDto.getError() != null) {
            predictionResponseRul.setError(predictionResponseRulDto.getError());
        }
        if (predictionResponseRulDto.getPredictionRul() != null) {
            predictionResponseRul.setPredictedRul(predictionResponseRulDto.getPredictionRul());
        }
        return predictionResponseRul;
    }
}
