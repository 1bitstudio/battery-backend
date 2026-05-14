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
        batteryData.setTimeInS(csvRows.getTimeInS());
        batteryData.setTemperatureInC(csvRows.getTemperatureInC());
        batteryData.setInternalResistanceInOhm(csvRows.getInternalResistanceInOhm());
        batteryData.setFormFactor(csvRows.getFormFactor());
        batteryData.setAnodeComposition(csvRows.getAnodeComposition());
        batteryData.setCathodeComposition(csvRows.getCathodeComposition());
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
        csvRows.setTimeInS(batteryData.getTimeInS());
        csvRows.setTemperatureInC(batteryData.getTemperatureInC());
        csvRows.setInternalResistanceInOhm(batteryData.getInternalResistanceInOhm());
        csvRows.setFormFactor(batteryData.getFormFactor());
        csvRows.setAnodeComposition(batteryData.getAnodeComposition());
        csvRows.setCathodeComposition(batteryData.getCathodeComposition());
        return csvRows;
    }
}