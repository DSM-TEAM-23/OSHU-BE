package com.dsm.oshu.store.service;

import com.dsm.oshu.store.presentation.dto.CrowdStatusRequest;
import com.dsm.oshu.store.presentation.dto.CrowdStatusResponse;
import com.dsm.oshu.store.presentation.dto.StoreCardResponse;
import com.dsm.oshu.store.presentation.dto.StoreCreateRequest;
import com.dsm.oshu.store.presentation.dto.StoreDetailResponse;
import com.dsm.oshu.store.presentation.dto.StoreUpdateRequest;
import com.dsm.oshu.store.service.StoreDtoMapper;
import com.dsm.oshu.store.service.StoreReader;
import com.dsm.oshu.store.domain.Store;
import com.dsm.oshu.store.domain.StoreRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OwnerStoreService {
    private static final double DEFAULT_STORE_LATITUDE = 36.3628;
    private static final double DEFAULT_STORE_LONGITUDE = 127.3441;

    private final StoreRepository stores;
    private final StoreReader storeReader;
    private final StoreDtoMapper storeDtoMapper;

    public OwnerStoreService(StoreRepository stores, StoreReader storeReader, StoreDtoMapper storeDtoMapper) {
        this.stores = stores;
        this.storeReader = storeReader;
        this.storeDtoMapper = storeDtoMapper;
    }

    public List<StoreCardResponse> ownerStores(String ownerLoginId) {
        return stores.findByOwnerLoginId(ownerLoginId).stream()
                .map(storeDtoMapper::toCard)
                .toList();
    }

    @Transactional
    public StoreDetailResponse createStore(String ownerLoginId, StoreCreateRequest request) {
        Store store = stores.save(new Store(
                request.name(),
                request.category(),
                request.customCategory(),
                request.description(),
                request.address(),
                DEFAULT_STORE_LATITUDE,
                DEFAULT_STORE_LONGITUDE,
                request.phone(),
                request.openingHours(),
                ownerLoginId));
        return storeDtoMapper.toDetail(store);
    }

    public StoreDetailResponse ownerStore(String ownerLoginId, Long storeId) {
        return storeDtoMapper.toDetail(storeReader.requireOwnedStore(ownerLoginId, storeId));
    }

    @Transactional
    public StoreDetailResponse updateStore(String ownerLoginId, Long storeId, StoreUpdateRequest request) {
        Store store = storeReader.requireOwnedStore(ownerLoginId, storeId);
        store.update(
                request.name(),
                request.category(),
                request.customCategory(),
                request.description(),
                request.address(),
                request.phone(),
                request.openingHours());
        return storeDtoMapper.toDetail(store);
    }

    @Transactional
    public CrowdStatusResponse updateCrowd(String ownerLoginId, Long storeId, CrowdStatusRequest request) {
        Store store = storeReader.requireOwnedStore(ownerLoginId, storeId);
        store.updateCrowd(request.level(), request.estimatedWaitingMinutes());
        return storeDtoMapper.toCrowd(store);
    }
}
