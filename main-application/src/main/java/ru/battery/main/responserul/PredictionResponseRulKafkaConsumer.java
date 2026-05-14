package ru.battery.main.responserul;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.requests.Request;
import ru.battery.main.requests.RequestStorage;
import ru.battery.main.responserul.dto.PredictionResponseRulDto;
import ru.battery.main.responserul.dto.PredictionResponseRulMapper;

@Service
@RequiredArgsConstructor
public class PredictionResponseRulKafkaConsumer {
    private final PredictionResponseRulStorage predictionResponseRulStorage;
    private final RequestStorage requestStorage;

    @KafkaListener(topics = "rul_responses", groupId = "soh-ml-worker", containerFactory = "mlRulContainerFactory")
    public void consumePredictionResponseRul(PredictionResponseRulDto predictionResponseRulDto) {
        Request request = requestStorage.findById(predictionResponseRulDto.getRequestId()).orElseThrow(() ->
                new NotFoundException("Запрос с ID: " + predictionResponseRulDto.getRequestId() + " не найден."));
        PredictionResponseRul predictionResponseRul = PredictionResponseRulMapper.toPredictionResponseFromPredictionResponseDto(
                predictionResponseRulDto);
        predictionResponseRul.setRequest(request);
        predictionResponseRulStorage.save(predictionResponseRul);
    }
}
