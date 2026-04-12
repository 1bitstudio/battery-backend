package ru.battery.main.data;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.battery.main.data.dto.CreateBatteryDataDto;
import ru.battery.main.data.dto.CsvRows;

import java.util.List;

@RestController
@RequestMapping("/battery-data")
@RequiredArgsConstructor
public class BatteryDataController {
    private final BatteryDataService batteryDataService;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateBatteryDataDto createBatteryData(@PathVariable Long userId,
                                                  @RequestParam(required = false) String requestName,
                                                  @RequestParam("file") MultipartFile file) {
        return batteryDataService.sendDataToMl(userId, requestName, file);
    }

    @GetMapping("/{userId}/{requestId}")
    public List<CsvRows> getDataByRequestId(@PathVariable Long userId, @PathVariable Long requestId) {
        return batteryDataService.getDataByRequestId(userId, requestId);
    }
}
