package com.dsm.oshu.store.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOwnerLoginId(String ownerLoginId);
}
