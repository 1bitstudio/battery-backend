package ru.battery.main.soh;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.requests.Request;
import ru.battery.main.requests.RequestStorage;
import ru.battery.main.soh.dto.PredictionResponseSohDto;
import ru.battery.main.soh.dto.PredictionResponseSohMapper;

@Service
@RequiredArgsConstructor
public class PredictionResponseSohKafkaConsumer {
    private final PredictionResponseSohStorage predictionResponseSohStorage;
    private final RequestStorage requestStorage;

    @KafkaListener(topics = "soh_responses", groupId = "soh-ml-worker", containerFactory = "mlSohContainerFactory")
    public void consumePredictionResponseSoh(PredictionResponseSohDto predictionResponseSohDto) {
        Request request = requestStorage.findById(predictionResponseSohDto.getRequestId()).orElseThrow(() ->
                new NotFoundException("Запрос с ID: " + predictionResponseSohDto.getRequestId() + " не найден."));
        PredictionResponseSoh predictionResponseSoh = PredictionResponseSohMapper
                .fromPredictionResponseSohDtoToPredictionResponseSoh(predictionResponseSohDto);
        predictionResponseSoh.setRequest(request);
        predictionResponseSohStorage.save(predictionResponseSoh);
    }
}
