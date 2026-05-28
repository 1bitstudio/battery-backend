package ru.battery.main.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.battery.main.data.dto.*;
import ru.battery.main.exceptions.ForbiddenException;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.exceptions.ValidationException;
import ru.battery.main.requests.ModelTypes;
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
@Slf4j
public class BatteryDataService {
    private final BatteryDataStorage batteryDataStorage;
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;
    private final KafkaTemplate<String, FileUploadEventDto> kafkaTemplateForFileUpload;
    private final KafkaTemplate<String, MlRequestForSoh> kafkaTemplateForSoh;
    private final KafkaTemplate<String, MlRequestForRecommendation> kafkaTemplateForRecommendation;
    private final S3StorageService s3StorageService;

    public Long sendDataToMl(Long userId, String requestName, String stringModelType, MultipartFile file) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден"));
        if (user.getAccountType() != AccountType.CONFIGURED) {
            throw new ForbiddenException("У пользователя с ID = " + userId + " не подтвержденный аккаунт. " +
                    "Доступ запрещен");
        }

        if (stringModelType == null || stringModelType.isBlank()) {
            throw new ValidationException("Тип модели должен быть указан");
        }

        ModelTypes modelType;

        try {
            modelType = ModelTypes.valueOf(stringModelType);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Тип модели указан не верно");
        }

        Request request = requestStorage.save(createRequest(user, requestName, modelType, file));

        StoredFileDto storedFileDto = s3StorageService.uploadFile(request.getId(), file);
        FileUploadEventDto fileUploadEventDto = new FileUploadEventDto(userId, request.getId(),
                storedFileDto.getBucket(), storedFileDto.getObjectKey(), storedFileDto.getOriginalFilename(),
                modelType.name());

        kafkaTemplateForFileUpload.send("file-data", fileUploadEventDto);

        log.info("В сервис обработки файлов отправлены данные: \n{}", fileUploadEventDto);

        return request.getId();
    }

    public void sendDataToMlWithManyFiles(Long userId, String requestName, Map<String, MultipartFile> files) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден"));
        if (user.getAccountType() != AccountType.CONFIGURED) {
            throw new ForbiddenException("У пользователя с ID = " + userId + " не подтвержденный аккаунт. " +
                    "Доступ запрещен");
        }

        if (files == null || files.isEmpty()) {
            throw new ValidationException("Файлы не были переданы");
        }

        Set<String> namesModels = files.keySet();

        for (String nameModel: namesModels) {
            ModelTypes modelType;
            try {
                modelType = ModelTypes.valueOf(nameModel);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Тип модели указан не верно");
            }

            Request request = requestStorage.save(createRequest(user, requestName, modelType, files.get(nameModel)));
            StoredFileDto storedFileDto = s3StorageService.uploadFile(request.getId(), files.get(nameModel));
            FileUploadEventDto fileUploadEventDto = new FileUploadEventDto(userId, request.getId(),
                    storedFileDto.getBucket(), storedFileDto.getObjectKey(), storedFileDto.getOriginalFilename(),
                    nameModel);
            kafkaTemplateForFileUpload.send("file-data", fileUploadEventDto);
        }
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
        MlRequestForSoh mlRequestForSoh = new MlRequestForSoh(requestId, targetCycles, request.getModelType().name(),
                fromCsvRowsToBatteryInputData(csvRows, obsCycles));

        kafkaTemplateForSoh.send("soh-data", mlRequestForSoh);
    }

    public void createRecommendation(Long userId, Long requestId, Double nominalVoltageInV) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден"));
        Request request = requestStorage.findById(requestId).orElseThrow(() -> new NotFoundException("Запроса с ID = " +
                requestId + " не найден"));

        if (!request.getUser().equals(user)) {
            throw new ForbiddenException("У пользователя с ID = " + userId + "нет доступа к запросу с ID =" + requestId);
        }

        if (nominalVoltageInV == null) {
            throw new ValidationException("Переменная nominalVoltageInV не была передана");
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

        MlRequestForRecommendation mlRequestForRecommendation = new MlRequestForRecommendation(requestId,
                nominalVoltageInV, request.getModelType().name(), fromCsvRowsToBatteryInputData(csvRows, obsCycles));

        kafkaTemplateForRecommendation.send("recommend-data", mlRequestForRecommendation);
    }

    private Request createRequest(User user, String requestName, ModelTypes modelType, MultipartFile file) {
        Request request = new Request();
        request.setUser(user);
        request.setCreatedAt(LocalDateTime.now());

        request.setModelType(modelType);

        if (requestName != null && !requestName.isBlank()) {
            request.setRequestName(requestName);
        } else {
            request.setRequestName(file.getOriginalFilename());
        }
        return request;
    }

    private BatteryInputData fromCsvRowsToBatteryInputData(List<CsvRows> csvRows, int obsCycles) {
        if (csvRows.isEmpty()) {
            throw new ValidationException("CSV файл не содержит данных");
        }

        CsvRows firstRow = csvRows.get(0);

        Double nominalCapacity = firstRow.getNominalCapacityInAh();
        String formFactor = firstRow.getFormFactor();

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

            CycleData cycleData = new CycleData(
                    voltages,
                    currents,
                    chargeCapacities,
                    dischargeCapacities,
                    timeInS,
                    temperatureInC
            );

            cycleDataList.add(cycleData);
        }

        return new BatteryInputData(nominalCapacity, formFactor, cycleDataList, obsCycles);
    }

}
