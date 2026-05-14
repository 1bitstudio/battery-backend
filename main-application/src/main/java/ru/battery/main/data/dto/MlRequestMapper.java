package ru.battery.main.data.dto;

import java.util.List;

public class MlRequestMapper {
    public static MlRequestForSoh fromMlRequestForRulToMlRequestForSoh(MlRequestForRul mlRequestForRul,
                                                                       List<Integer> cycleNumbers) {
        MlRequestForSoh mlRequestForSoh = new MlRequestForSoh();
        mlRequestForSoh.setRequestId(mlRequestForRul.getRequestId());
        mlRequestForSoh.setBatteryInputData(mlRequestForRul.getBatteryInputData());
        mlRequestForSoh.setTargetCycles(cycleNumbers);
        return mlRequestForSoh;
    }
}
