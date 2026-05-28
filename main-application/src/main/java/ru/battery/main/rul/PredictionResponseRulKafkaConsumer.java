package ru.battery.main.rul;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.requests.Request;
import ru.battery.main.requests.RequestStorage;
import ru.battery.main.rul.dto.PredictionResponseRulDto;
import ru.battery.main.rul.dto.PredictionResponseRulMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionResponseRulKafkaConsumer {
    private final PredictionResponseRulStorage predictionResponseRulStorage;
    private final RequestStorage requestStorage;

    @KafkaListener(topics = "rul_responses", groupId = "soh-ml-worker", containerFactory = "mlRulContainerFactory")
    public void consumePredictionResponseRul(PredictionResponseRulDto predictionResponseRulDto) {
        log.info("Из сервиса расчета RUL пришли данные: \n{}", predictionResponseRulDto);

        Request request = requestStorage.findById(predictionResponseRulDto.getRequestId()).orElseThrow(() ->
                new NotFoundException("Запрос с ID: " + predictionResponseRulDto.getRequestId() + " не найден."));
        PredictionResponseRul predictionResponseRul = PredictionResponseRulMapper.toPredictionResponseFromPredictionResponseDto(
                predictionResponseRulDto);
        predictionResponseRul.setRequest(request);
        predictionResponseRulStorage.save(predictionResponseRul);
    }
}
