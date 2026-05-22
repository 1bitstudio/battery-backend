package ru.battery.main.rul;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.battery.main.reports.dto.ReportUserDto;

import java.util.List;
import java.util.Optional;

public interface PredictionResponseRulStorage extends JpaRepository<PredictionResponseRul, Long> {
    Optional<PredictionResponseRul> findByRequestId(Long requestId);

    @Query("SELECT r.requestName AS filename, pr.predictedRul AS rul " +
            "FROM PredictionResponseRul pr JOIN pr.request r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<ReportUserDto> findUserReportRows(@Param("userId") Long userId);
}
