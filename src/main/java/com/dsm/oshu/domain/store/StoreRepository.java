package com.dsm.oshu.domain.store;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOwnerLoginId(String ownerLoginId);
}
