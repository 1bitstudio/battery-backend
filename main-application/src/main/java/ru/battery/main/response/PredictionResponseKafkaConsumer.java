package ru.battery.main.response;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.requests.Request;
import ru.battery.main.requests.RequestStorage;
import ru.battery.main.response.dto.PredictionResponseDto;
import ru.battery.main.response.dto.PredictionResponseMapper;

@Service
@RequiredArgsConstructor
public class PredictionResponseKafkaConsumer {
    private final PredictionResponseStorage predictionResponseStorage;
    private final RequestStorage requestStorage;

    @KafkaListener(topics = "soh_responses", groupId = "soh-ml-worker", containerFactory = "mlContainerFactory")
    public void consumePredictionResponse(PredictionResponseDto predictionResponseDto) {
        Request request = requestStorage.findById(predictionResponseDto.getRequestId()).orElseThrow(() ->
                new NotFoundException("Запрос с ID: " + predictionResponseDto.getRequestId() + " не найден."));
        PredictionResponse predictionResponse = PredictionResponseMapper.toPredictionResponseFromPredictionResponseDto(
                predictionResponseDto);
        predictionResponse.setRequest(request);
        predictionResponseStorage.save(predictionResponse);
    }
}
