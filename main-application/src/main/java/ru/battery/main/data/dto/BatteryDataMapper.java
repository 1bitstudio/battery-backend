package ru.battery.main.data.dto;

import ru.battery.main.data.BatteryData;
import ru.battery.main.requests.Request;


public class BatteryDataMapper {
    public static BatteryData toBatteryDataFromCsvRows(CsvRows csvRows, Request request) {
        BatteryData batteryData = new BatteryData();
        batteryData.setRequest(request);
        batteryData.setCycleNumber(csvRows.getCycleNumber());
        batteryData.setPointIndex(csvRows.getPointIndex());
        batteryData.setVoltageInV(csvRows.getVoltageInV());
        batteryData.setCurrentInA(csvRows.getCurrentInA());
        batteryData.setChargeCapacityInAh(csvRows.getChargeCapacityInAh());
        batteryData.setDischargeCapacityInAh(csvRows.getDischargeCapacityInAh());
        batteryData.setNominalCapacityInAh(csvRows.getNominalCapacityInAh());
        batteryData.setSocStart(csvRows.getSocStart());
        batteryData.setSocEnd(csvRows.getSocEnd());
        return batteryData;
    }

    public static CsvRows toCsvRowsFromBatteryData(BatteryData batteryData) {
        CsvRows csvRows = new CsvRows();
        csvRows.setCycleNumber(batteryData.getCycleNumber());
        csvRows.setPointIndex(batteryData.getPointIndex());
        csvRows.setVoltageInV(batteryData.getVoltageInV());
        csvRows.setCurrentInA(batteryData.getCurrentInA());
        csvRows.setChargeCapacityInAh(batteryData.getChargeCapacityInAh());
        csvRows.setDischargeCapacityInAh(batteryData.getDischargeCapacityInAh());
        csvRows.setNominalCapacityInAh(batteryData.getNominalCapacityInAh());
        csvRows.setSocStart(batteryData.getSocStart());
        csvRows.setSocEnd(batteryData.getSocEnd());
        return csvRows;
    }
}
