package com.dsm.oshu.recommendation.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HourlyOrderStatisticRepository extends JpaRepository<HourlyOrderStatistic, Long> {
    Optional<HourlyOrderStatistic> findByStoreIdAndOrderDateAndHour(Long storeId, LocalDate orderDate, int hour);

    List<HourlyOrderStatistic> findByStoreIdAndOrderDateOrderByHourAsc(Long storeId, LocalDate orderDate);
}
