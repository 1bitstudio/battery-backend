package ru.battery.main.data;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.battery.main.data.dto.*;
import ru.battery.main.exceptions.ForbiddenException;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.exceptions.ValidationException;
import ru.battery.main.requests.Request;
import ru.battery.main.requests.RequestStorage;
import ru.battery.main.users.AccountType;
import ru.battery.main.users.User;
import ru.battery.main.users.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatteryDataService {
    private final BatteryDataStorage batteryDataStorage;
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;
    private final KafkaTemplate<String, FileUploadEventDto> kafkaTemplateForFileUpload;
    private final KafkaTemplate<String, MlRequestForSoh> kafkaTemplateForSoh;
    private final S3StorageService s3StorageService;

    public void sendDataToMl(Long userId, String requestName, MultipartFile file) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден"));
        if (user.getAccountType() != AccountType.CONFIGURED) {
            throw new ForbiddenException("У пользователя с ID = " + userId + " не подтвержденный аккаунт. " +
                    "Доступ запрещен");
        }

        Request request = requestStorage.save(createRequest(user, requestName, file));

        StoredFileDto storedFileDto = s3StorageService.uploadFile(request.getId(), file);
        FileUploadEventDto fileUploadEventDto = new FileUploadEventDto(userId, request.getId(),
                storedFileDto.getBucket(), storedFileDto.getObjectKey(), storedFileDto.getOriginalFilename());

        kafkaTemplateForFileUpload.send("file-data", fileUploadEventDto);
    }

    public void sendDataToMlWithManyFiles(Long userId, String requestName, List<MultipartFile> files) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден"));
        if (user.getAccountType() != AccountType.CONFIGURED) {
            throw new ForbiddenException("У пользователя с ID = " + userId + " не подтвержденный аккаунт. " +
                    "Доступ запрещен");
        }

        if (files == null || files.isEmpty()) {
            throw new ValidationException("Файлы не были переданы");
        }

        for (MultipartFile file : files) {
            Request request = requestStorage.save(createRequest(user, requestName, file));
            StoredFileDto storedFileDto = s3StorageService.uploadFile(request.getId(), file);
            FileUploadEventDto fileUploadEventDto = new FileUploadEventDto(userId, request.getId(),
                    storedFileDto.getBucket(), storedFileDto.getObjectKey(), storedFileDto.getOriginalFilename());
            kafkaTemplateForFileUpload.send("file-data", fileUploadEventDto);
        }
    }

    public void createSohPrediction(Long userId, Long requestId, List<Integer> targetCycles) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден"));
        Request request = requestStorage.findById(requestId).orElseThrow(() -> new NotFoundException("Запроса с ID = " +
                requestId + " не найден"));

        if (!request.getUser().equals(user)) {
            throw new ForbiddenException("У пользователя с ID = " + userId + "нет доступа к запросу с ID =" + requestId);
        }

        if (targetCycles.isEmpty()) {
            throw new ValidationException("Значения не переданы или переданы неправильно");
        }

        List<Integer> notPositiveCycleNumbers = targetCycles.stream().filter(cycleNumber -> cycleNumber < 0)
                .toList();
        if (!notPositiveCycleNumbers.isEmpty()) {
            throw new ValidationException("Все номера циклов должны быть положительными");
        }

        List<BatteryData> batteryDataForRequest = batteryDataStorage.findAllByRequestId(requestId);
        if (batteryDataForRequest.isEmpty()) {
            throw new ValidationException("Данных для данного запроса не существует");
        }

        List<CsvRows> csvRows = batteryDataForRequest.stream().map(BatteryDataMapper::toCsvRowsFromBatteryData).toList();
        int obsCycles = (int) csvRows.stream()
                .map(CsvRows::getCycleNumber)
                .distinct()
                .count();
        MlRequestForSoh mlRequestForSoh = toMlRequestForSoh(csvRows, obsCycles, requestId, targetCycles);

        kafkaTemplateForSoh.send("soh-data", mlRequestForSoh);
    }

    private Request createRequest(User user, String requestName, MultipartFile file) {
        Request request = new Request();
        request.setUser(user);
        request.setCreatedAt(LocalDateTime.now());

        if (requestName != null && !requestName.isBlank()) {
            request.setRequestName(requestName);
        } else {
            request.setRequestName(file.getOriginalFilename());
        }
        return request;
    }

    private MlRequestForSoh toMlRequestForSoh(List<CsvRows> csvRows, int obsCycles, Long requestId,
                                              List<Integer> cycleNumbers) {
        if (csvRows.isEmpty()) {
            throw new ValidationException("CSV файл не содержит данных");
        }

        CsvRows firstRow = csvRows.get(0);

        Double nominalCapacity = firstRow.getNominalCapacityInAh();

        Map<Integer, List<CsvRows>> groupedByCycle = csvRows.stream()
                .collect(Collectors.groupingBy(
                        CsvRows::getCycleNumber,
                        TreeMap::new,
                        Collectors.toList()
                ));
        List<CycleData> cycleDataList = new ArrayList<>();
        for (List<CsvRows> entry : groupedByCycle.values()) {
            List<CsvRows> cycleRows = entry.stream()
                    .sorted(Comparator.comparing(CsvRows::getPointIndex))
                    .toList();

            List<Double> voltages = cycleRows.stream()
                    .map(CsvRows::getVoltageInV)
                    .toList();

            List<Double> currents = cycleRows.stream()
                    .map(CsvRows::getCurrentInA)
                    .toList();

            List<Double> chargeCapacities = cycleRows.stream()
                    .map(CsvRows::getChargeCapacityInAh)
                    .toList();

            List<Double> dischargeCapacities = cycleRows.stream()
                    .map(CsvRows::getDischargeCapacityInAh)
                    .toList();

            List<Double> timeInS = cycleRows.stream()
                    .map(CsvRows::getTimeInS)
                    .toList();

            List<Double> temperatureInC = cycleRows.stream()
                    .map(CsvRows::getTemperatureInC)
                    .toList();

            List<Double> internalResistanceInOhm = cycleRows.stream()
                    .map(CsvRows::getInternalResistanceInOhm)
                    .toList();

            CycleData cycleData = new CycleData(
                    voltages,
                    currents,
                    chargeCapacities,
                    dischargeCapacities,
                    timeInS,
                    temperatureInC,
                    internalResistanceInOhm
            );

            cycleDataList.add(cycleData);
        }

        BatteryInputData batteryInputData = new BatteryInputData(nominalCapacity, cycleDataList, obsCycles);

        return new MlRequestForSoh(requestId,cycleNumbers, batteryInputData);
    }

    public List<CsvRows> getDataByRequestId(Long userId, Long requestId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден"));
        Request request = requestStorage.findById(requestId).orElseThrow(() -> new NotFoundException("Запроса с ID = " +
                requestId + " не существует"));

        if (!request.getUser().equals(user)) {
            throw new ForbiddenException("У пользователя с ID = " + userId + " нет доступа к запросу " + requestId);
        }

        return batteryDataStorage.findAllByRequestId(requestId).stream()
                .map(BatteryDataMapper::toCsvRowsFromBatteryData).toList();
    }
}
