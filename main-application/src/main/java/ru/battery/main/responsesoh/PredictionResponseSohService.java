package ru.battery.main.responsesoh;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.ForbiddenException;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.requests.Request;
import ru.battery.main.requests.RequestStorage;
import ru.battery.main.responsesoh.dto.PredictionResponseSohMapper;
import ru.battery.main.responsesoh.dto.PredictionResponseSohTotalDto;
import ru.battery.main.users.User;
import ru.battery.main.users.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionResponseSohService {
    private final PredictionResponseSohStorage predictionResponseSohStorage;
    private final UserStorage userStorage;
    private final RequestStorage requestStorage;


    public List<PredictionResponseSohTotalDto> getPredictionResponseSoh(Long userId, Long requestId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID: " + userId + " не найден."));
        Request request = requestStorage.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с ID: " + requestId + " не найден."));
        if (!request.getUser().equals(user)) {
            throw new ForbiddenException("У пользователя с ID = " + userId + " нет доступа к запросу с ID = " +
                    requestId);
        }
        List<PredictionResponseSoh> predictionResponseSoh = predictionResponseSohStorage.findAllByRequestId(requestId);
        return predictionResponseSoh.stream().filter(predictionResponseSoh1 ->
                predictionResponseSoh1.getError() == null).map(
                        PredictionResponseSohMapper::fromPredictionResponseSohToPredictionResponseSohTotalDto).toList();
    }
}
