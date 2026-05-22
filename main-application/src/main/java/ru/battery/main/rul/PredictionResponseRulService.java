package ru.battery.main.rul;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.ForbiddenException;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.exceptions.ValidationException;
import ru.battery.main.requests.Request;
import ru.battery.main.requests.RequestStorage;
import ru.battery.main.rul.dto.PredictionResponseRulTotalDto;
import ru.battery.main.users.User;
import ru.battery.main.users.UserStorage;

@Service
@RequiredArgsConstructor
public class PredictionResponseRulService {
    private final UserStorage userStorage;
    private final RequestStorage requestStorage;
    private final PredictionResponseRulStorage predictionResponseRulStorage;

    public PredictionResponseRulTotalDto getPredictionResponse(Long userId, Long requestId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID: " + userId + " не найден."));
        Request request = requestStorage.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с ID: " + requestId + " не найден."));
        if (!request.getUser().equals(user)) {
            throw new ForbiddenException("У пользователя с ID = " + userId + " нет доступа к запросу с ID = " +
                    requestId);
        }

        PredictionResponseRul predictionResponseRul = predictionResponseRulStorage.findByRequestId(requestId).orElseThrow(() ->
                new NotFoundException("У запроса с ID = " + requestId + " еще нет результат."));

        if (predictionResponseRul.getError() != null) {
            throw new ValidationException("Ошибка: " + predictionResponseRul.getError());
        }

        return new PredictionResponseRulTotalDto(requestId, predictionResponseRul.getPredictedRul());
    }
}
