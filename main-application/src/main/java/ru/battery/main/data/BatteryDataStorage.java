package ru.battery.main.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatteryDataStorage extends JpaRepository<BatteryData, Long> {
    List<BatteryData> findAllByRequestId(Long requestId);
}
