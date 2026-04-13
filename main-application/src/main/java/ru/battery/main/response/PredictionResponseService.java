package ru.battery.main.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.ForbiddenException;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.exceptions.ValidationException;
import ru.battery.main.requests.Request;
import ru.battery.main.requests.RequestStorage;
import ru.battery.main.response.dto.PredictionResponseTotalDto;
import ru.battery.main.users.User;
import ru.battery.main.users.UserStorage;

@Service
@RequiredArgsConstructor
public class PredictionResponseService {
    private final UserStorage userStorage;
    private final RequestStorage requestStorage;
    private final PredictionResponseStorage predictionResponseStorage;

    public PredictionResponseTotalDto getPredictionResponse(Long userId, Long requestId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID: " + userId + " не найден."));
        Request request = requestStorage.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с ID: " + requestId + " не найден."));
        if (!request.getUser().equals(user)) {
            throw new ForbiddenException("У пользователя с ID = " + userId + " нет доступа к запросу с ID = " +
                    requestId);
        }

        PredictionResponse predictionResponse = predictionResponseStorage.findByRequestId(requestId).orElseThrow(() ->
                new NotFoundException("У запроса с ID = " + requestId + " еще нет результат."));

        if (predictionResponse.getError() != null) {
            throw new ValidationException("Ошибка: " + predictionResponse.getError());
        }

        return new PredictionResponseTotalDto(requestId, predictionResponse.getPredictedSoh(),
                predictionResponse.getPredictedSohPercent(), predictionResponse.getTargetCycle());
    }
}
