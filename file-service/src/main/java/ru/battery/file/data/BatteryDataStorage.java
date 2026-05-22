package ru.battery.file.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BatteryDataStorage extends JpaRepository<BatteryDataFile, Long> {
}
