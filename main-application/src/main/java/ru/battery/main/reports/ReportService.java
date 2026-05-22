package ru.battery.main.reports;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.battery.main.exceptions.NotFoundException;
import ru.battery.main.exceptions.ValidationException;
import ru.battery.main.reports.dto.ReportUserDto;
import ru.battery.main.rul.PredictionResponseRulStorage;
import ru.battery.main.users.UserStorage;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final UserStorage userStorage;
    private final PredictionResponseRulStorage predictionResponseRulStorage;


    public byte[] getReportByUser(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден"));

        List<ReportUserDto> reportsUserDto = predictionResponseRulStorage.findUserReportRows(userId);

        if (reportsUserDto.isEmpty()) {
            throw new ValidationException("У пользователя еще нет запросов");
        }

        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');
        csv.append("Name;RUL\n");

        for (ReportUserDto reportUserDto : reportsUserDto) {
            csv.append(escapeCsv(reportUserDto.getFilename()))
                    .append(";")
                    .append(reportUserDto.getRul() == null ? "" : reportUserDto.getRul())
                    .append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");

        if (escaped.contains(";") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }
}
