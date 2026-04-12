package ru.battery.main.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.requests.dto.RequestDto;
import ru.battery.main.requests.dto.RequestMapper;
import ru.battery.main.users.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;

    public List<RequestDto> getUserRequests(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден"));

        return requestStorage.findAllByUserId(userId).stream().map(RequestMapper::toDto).toList();
    }

    public void deleteByRequestId(Long requestId) {
        requestStorage.findById(requestId).orElseThrow(() -> new NotFoundException("Запроса с ID = " +
                requestId + " не существует"));
        requestStorage.deleteById(requestId);
    }
}
