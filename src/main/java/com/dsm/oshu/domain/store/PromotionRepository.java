package com.dsm.oshu.domain.store;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByStoreId(Long storeId);
}
