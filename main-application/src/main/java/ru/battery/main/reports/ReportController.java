package ru.battery.main.reports;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/{userId}/report")
    public ResponseEntity<byte[]> downloadReportByUser(@PathVariable Long userId) {
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("battery_report_user_" + userId + ".csv", StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(reportService.getReportByUser(userId));
    }
}
