package ru.battery.main.data;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.battery.main.data.dto.CreateBatteryDataDto;
import ru.battery.main.data.dto.CsvRows;
import ru.battery.main.users.User;

import java.util.List;

@RestController
@RequestMapping("/battery-data")
@RequiredArgsConstructor
public class BatteryDataController {
    private final BatteryDataService batteryDataService;

    @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping("/soh/{userId}/{requestId}")
    public void createSohPrediction(@PathVariable Long userId,
                                      @PathVariable Long requestId,
                                      @RequestParam List<Integer> targetCycles) {
        batteryDataService.createSohPrediction(userId, requestId, targetCycles);
    }

    @PostMapping("/upload")
    public CreateBatteryDataDto uploadBatteryData(@AuthenticationPrincipal User user,
                                                  @RequestParam MultipartFile file,
                                                  @RequestParam(required = false) String requestName) {
        return batteryDataService.sendDataToMl(user.getId(), requestName, file);
    }
}
