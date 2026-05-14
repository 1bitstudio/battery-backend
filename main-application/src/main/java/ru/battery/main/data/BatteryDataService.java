package ru.battery.main.data;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatteryDataService {
    private final BatteryDataStorage batteryDataStorage;
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;
    private final KafkaTemplate<String, MlRequestForRul> kafkaTemplateForRul;
    private final KafkaTemplate<String, MlRequestForSoh> kafkaTemplateForSoh;

    private final Set<String> requiredBatteryDateHeaders = Set.of(
            "cycle_number",
            "point_index",
            "voltage_in_V",
            "current_in_A",
            "charge_capacity_in_Ah",
            "discharge_capacity_in_Ah",
            "nominal_capacity_in_Ah",
            "time_in_s",
            "temperature_in_C",
            "internal_resistance_in_ohm",
            "form_factor",
            "anode_composition",
            "cathode_composition"
    );

    public CreateBatteryDataDto sendDataToMl(Long userId, String requestName, MultipartFile file) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден"));
        if (user.getAccountType() != AccountType.CONFIGURED) {
            throw new ForbiddenException("У пользователя с ID = " + userId + " не подтвержденный аккаунт. " +
                    "Доступ запрещен");
        }

        validateFile(file);

        List<CsvRows> csvRows = parseCsv(file);

        validateCommonFields(csvRows);

        int obsCycles = (int) csvRows.stream()
                .map(CsvRows::getCycleNumber)
                .distinct()
                .count();

        Request request = requestStorage.save(createRequest(user, requestName, file));

        List<BatteryData> listForSaveData = new ArrayList<>();
        for (CsvRows row : csvRows) {
            listForSaveData.add(BatteryDataMapper.toBatteryDataFromCsvRows(row, request));
        }

        batteryDataStorage.saveAll(listForSaveData);

        MlRequestForRul mlRequestForRul = toMlRequestForRul(csvRows, obsCycles, request.getId());

        kafkaTemplateForRul.send("rul-data", mlRequestForRul);

        return new CreateBatteryDataDto(request.getId(), csvRows);
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
            throw new ValidationException("Данные для данного запроса не существуют");
        }

        List<CsvRows> csvRows = batteryDataForRequest.stream().map(BatteryDataMapper::toCsvRowsFromBatteryData).toList();
        int obsCycles = (int) csvRows.stream()
                .map(CsvRows::getCycleNumber)
                .distinct()
                .count();
        MlRequestForSoh mlRequestForSoh = MlRequestMapper.fromMlRequestForRulToMlRequestForSoh(toMlRequestForRul(csvRows,
                        obsCycles, request.getId()), targetCycles);

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

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("Файл не передан или пуст");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            throw new ValidationException("Разрешены файлы только формата .csv");
        }
    }

    private List<CsvRows> parseCsv(MultipartFile file) {
        try (
            Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            CSVParser csvParser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .build()
                    .parse(reader)
        ) {
            validateHeaders(csvParser.getHeaderMap().keySet());
            List<CsvRows> rows = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                CsvRows row = new CsvRows(
                        parseInteger(record, "cycle_number"),
                        parseInteger(record, "point_index"),
                        parseDouble(record, "voltage_in_V"),
                        parseDouble(record, "current_in_A"),
                        parseDouble(record, "charge_capacity_in_Ah"),
                        parseDouble(record, "discharge_capacity_in_Ah"),
                        parseDouble(record, "nominal_capacity_in_Ah"),
                        parseDouble(record, "time_in_s"),
                        parseDouble(record, "temperature_in_C"),
                        parseDouble(record, "internal_resistance_in_ohm"),
                        parseString(record, "form_factor"),
                        parseString(record, "anode_composition"),
                        parseString(record, "cathode_composition")
                );
                rows.add(row);
            }
            return rows;
            } catch (IOException e) {
            throw new ValidationException("Ошибка чтения CSV файла");
        }
    }

    private Integer parseInteger(CSVRecord record, String column) {
        String value = record.get(column);

        if (value == null || value.isBlank()) {
            throw new ValidationException("Пустое значение в колонке: " + column);
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Некорректное целое число в колонке " + column + ": " + value);
        }
    }

    private Double parseDouble(CSVRecord record, String column) {
        String value = record.get(column);

        if (value == null || value.isBlank()) {
            throw new ValidationException("Пустое значение в колонке: " + column);
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Некорректное число в колонке " + column + ": " + value);
        }
    }

    private String parseString(CSVRecord record, String column) {
        String value = record.get(column);

        if (value == null || value.isBlank()) {
            throw new ValidationException("Пустое значение в колонке: " + column);
        }

        return value.trim();
    }

    private void validateHeaders(Set<String> actualHeaders) {
        Set<String> normalizedHeaders = actualHeaders.stream()
                .map(String::trim)
                .collect(Collectors.toSet());

        List<String> missingHeaders = requiredBatteryDateHeaders.stream()
                .filter(header -> !normalizedHeaders.contains(header))
                .sorted()
                .toList();

        if (!missingHeaders.isEmpty()) {
            throw new ValidationException("В CSV отсутствуют обязательные поля: " + missingHeaders);
        }
    }

    private void validateCommonFields(List<CsvRows> csvRows) {
        if (csvRows.isEmpty()) {
            throw new ValidationException("CSV файл не содержит данных");
        }

        CsvRows firstRow = csvRows.get(0);

        Double expectedNominalCapacity = firstRow.getNominalCapacityInAh();
        String expectedFormFactor = firstRow.getFormFactor();
        String expectedAnodeComposition = firstRow.getAnodeComposition();
        String expectedCathodeComposition = firstRow.getCathodeComposition();

        for (CsvRows row : csvRows) {
            if (!expectedNominalCapacity.equals(row.getNominalCapacityInAh())) {
                throw new ValidationException("Во всех строках nominal_capacity_in_Ah должен быть одинаковым");
            }
            if (!expectedFormFactor.equals(row.getFormFactor())) {
                throw new ValidationException("Во всех строках form_factor должен быть одинаковым");
            }
            if (!expectedAnodeComposition.equals(row.getAnodeComposition())) {
                throw new ValidationException("Во всех строках anode_composition должен быть одинаковым");
            }
            if (!expectedCathodeComposition.equals(row.getCathodeComposition())) {
                throw new ValidationException("Во всех строках cathode_composition должен быть одинаковым");
            }
        }
    }

    private MlRequestForRul toMlRequestForRul(List<CsvRows> csvRows, int obsCycles, Long requestId) {
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

        return new MlRequestForRul(requestId, batteryInputData);
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
