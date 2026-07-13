package com.dsm.oshu.store.service;

import com.dsm.oshu.store.domain.Store;
import com.dsm.oshu.store.domain.StoreRepository;
import com.dsm.oshu.store.exception.StoreNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class StoreReader {
    private final StoreRepository stores;

    public StoreReader(StoreRepository stores) {
        this.stores = stores;
    }

    public Store requireStore(Long storeId) {
        return stores.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException());
    }

    public Store requireOwnedStore(String ownerLoginId, Long storeId) {
        Store store = requireStore(storeId);
        assertOwner(ownerLoginId, store);
        return store;
    }

    public void assertOwner(String ownerLoginId, Store store) {
        if (!store.getOwnerLoginId().equals(ownerLoginId)) {
            throw new IllegalArgumentException("해당 가게를 관리할 권한이 없습니다.");
        }
    }
}
