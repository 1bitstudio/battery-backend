package ru.battery.main.soh.dto;

import ru.battery.main.soh.PredictionResponseSoh;

public class PredictionResponseSohMapper {
    public static PredictionResponseSoh fromPredictionResponseSohDtoToPredictionResponseSoh(
            PredictionResponseSohDto predictionResponseSohDto) {
        PredictionResponseSoh predictionResponseSoh = new PredictionResponseSoh();
        predictionResponseSoh.setStatus(predictionResponseSohDto.getStatus());
        if (predictionResponseSohDto.getError() != null) {
            predictionResponseSoh.setError(predictionResponseSohDto.getError());
        }
        if (predictionResponseSohDto.getResult() != null) {
            predictionResponseSoh.setPredictedSoh(predictionResponseSohDto.getResult().getPredictedSoh());
            predictionResponseSoh.setTargetCycle(predictionResponseSohDto.getResult().getTargetCycle());
        }
        return predictionResponseSoh;
    }

    public static PredictionResponseSohTotalDto fromPredictionResponseSohToPredictionResponseSohTotalDto(
            PredictionResponseSoh predictionResponseSoh) {
        PredictionResponseSohTotalDto predictionResponseSohTotalDto = new PredictionResponseSohTotalDto();
        predictionResponseSohTotalDto.setRequestId(predictionResponseSoh.getRequest().getId());
        predictionResponseSohTotalDto.setPredictedSoh(predictionResponseSoh.getPredictedSoh());
        predictionResponseSohTotalDto.setTargetCycle(predictionResponseSoh.getTargetCycle());
        return predictionResponseSohTotalDto;
    }
}
