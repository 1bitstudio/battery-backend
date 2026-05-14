package ru.battery.file;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.battery.file.data.BatteryDataFile;
import ru.battery.file.data.BatteryDataStorage;
import ru.battery.file.dto.*;
import ru.battery.file.exceptions.ValidationException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileKafkaConsumer {

    private final S3StorageService s3StorageService;
    private final BatteryDataStorage batteryDataStorage;
    private final KafkaTemplate<String, MlRequestForRul> kafkaTemplateForRul;

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

    @KafkaListener(topics = "file-data", groupId = "file-read-worker",
            containerFactory = "fileUploadEventContainerFactory")
    public void consume(FileUploadEventDto fileUploadEventDto) {
        try (ResponseInputStream<GetObjectResponse> inputStream =
                     s3StorageService.downloadFile(fileUploadEventDto.getBucket(), fileUploadEventDto.getObjectKey())) {

            processCsv(fileUploadEventDto.getRequestId(), inputStream);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка обработки файла из S3", e);
        }
    }

    private void processCsv(Long requestId, ResponseInputStream<GetObjectResponse> inputStream) {
        List<CsvRows> csvRows = parseCsv(inputStream);

        validateCommonFields(csvRows);

        int obsCycles = (int) csvRows.stream()
                .map(CsvRows::getCycleNumber)
                .distinct()
                .count();

        List<BatteryDataFile> listForSaveData = new ArrayList<>();
        for (CsvRows row : csvRows) {
            listForSaveData.add(BatteryDataFileMapper.toBatteryDataFileFromCsvRows(row, requestId));
        }

        batteryDataStorage.saveAll(listForSaveData);
        MlRequestForRul mlRequestForRul = toMlRequestForRul(csvRows, obsCycles, requestId);

        kafkaTemplateForRul.send("rul-data", mlRequestForRul);
    }

    private List<CsvRows> parseCsv(InputStream inputStream) {
        try (
                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
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
}
