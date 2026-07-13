package com.dsm.oshu.timesale.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSaleRepository extends JpaRepository<TimeSale, Long> {
    List<TimeSale> findByStoreId(Long storeId);
}
