package ru.battery.file.dto;

import ru.battery.file.data.BatteryDataFile;

public class BatteryDataFileMapper {
    public static BatteryDataFile toBatteryDataFileFromCsvRows(CsvRows csvRows, Long requestId) {
        BatteryDataFile batteryDataFile = new BatteryDataFile();
        batteryDataFile.setRequestId(requestId);
        batteryDataFile.setCycleNumber(csvRows.getCycleNumber());
        batteryDataFile.setPointIndex(csvRows.getPointIndex());
        batteryDataFile.setVoltageInV(csvRows.getVoltageInV());
        batteryDataFile.setCurrentInA(csvRows.getCurrentInA());
        batteryDataFile.setChargeCapacityInAh(csvRows.getChargeCapacityInAh());
        batteryDataFile.setDischargeCapacityInAh(csvRows.getDischargeCapacityInAh());
        batteryDataFile.setNominalCapacityInAh(csvRows.getNominalCapacityInAh());
        batteryDataFile.setTimeInS(csvRows.getTimeInS());
        batteryDataFile.setTemperatureInC(csvRows.getTemperatureInC());
        batteryDataFile.setFormFactor(csvRows.getFormFactor());
        return batteryDataFile;
    }
}
