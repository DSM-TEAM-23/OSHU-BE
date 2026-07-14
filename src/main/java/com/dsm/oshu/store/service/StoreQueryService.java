package com.dsm.oshu.store.service;

import com.dsm.oshu.common.presentation.dto.PageResponse;
import com.dsm.oshu.promotion.presentation.dto.PromotionResponse;
import com.dsm.oshu.store.presentation.dto.CrowdStatusResponse;
import com.dsm.oshu.store.presentation.dto.StoreCardResponse;
import com.dsm.oshu.store.presentation.dto.StoreDetailResponse;
import com.dsm.oshu.store.service.DistanceCalculator;
import com.dsm.oshu.common.service.PageUtils;
import com.dsm.oshu.promotion.service.PromotionDtoMapper;
import com.dsm.oshu.store.service.StoreDtoMapper;
import com.dsm.oshu.store.service.StoreReader;
import com.dsm.oshu.promotion.domain.PromotionRepository;
import com.dsm.oshu.store.domain.StoreRepository;
import com.dsm.oshu.store.infrastructure.PublicStore;
import com.dsm.oshu.store.infrastructure.PublicStoreDataClient;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StoreQueryService {
    private final StoreRepository stores;
    private final PromotionRepository promotions;
    private final PublicStoreDataClient publicStoreDataClient;
    private final StoreReader storeReader;
    private final StoreDtoMapper storeDtoMapper;
    private final PromotionDtoMapper promotionDtoMapper;

    public StoreQueryService(StoreRepository stores, PromotionRepository promotions,
                             PublicStoreDataClient publicStoreDataClient, StoreReader storeReader,
                             StoreDtoMapper storeDtoMapper, PromotionDtoMapper promotionDtoMapper) {
        this.stores = stores;
        this.promotions = promotions;
        this.publicStoreDataClient = publicStoreDataClient;
        this.storeReader = storeReader;
        this.storeDtoMapper = storeDtoMapper;
        this.promotionDtoMapper = promotionDtoMapper;
    }

    public PageResponse<StoreCardResponse> listStores(String keyword, String category, int page, int size) {
        List<StoreCardResponse> all = stores.findAll().stream()
                .filter(store -> contains(store.getName(), keyword) || contains(store.getAddress(), keyword))
                .filter(store -> category == null || category.isBlank() || store.getCategory().matches(category))
                .map(storeDtoMapper::toCard)
                .toList();
        return PageUtils.page(all, page, size);
    }

    public StoreDetailResponse getStore(Long storeId) {
        return storeDtoMapper.toDetail(storeReader.requireStore(storeId));
    }

    public List<StoreCardResponse> mapStores(double latitude, double longitude, int radius, boolean timeSaleOnly) {
        List<StoreCardResponse> registeredStores = stores.findAll().stream()
                .filter(store -> DistanceCalculator.distanceInMeters(
                        latitude, longitude, store.getLatitude(), store.getLongitude()) <= radius)
                .filter(store -> !timeSaleOnly || storeDtoMapper.hasActiveTimeSale(store.getId()))
                .map(storeDtoMapper::toCard)
                .toList();

        List<PublicStore> publicStores = publicStoreDataClient.findStoresInRadius(latitude, longitude, radius);
        Stream<StoreCardResponse> publicStoreResponses = timeSaleOnly ? Stream.empty() : publicStores.stream()
                .map(store -> new StoreCardResponse(null, store.name(), store.category(),
                        store.address(), store.latitude(), store.longitude(), null, null, false, true));

        return Stream.concat(registeredStores.stream(), publicStoreResponses)
                .toList();
    }

    public StoreCardResponse storeSummary(Long storeId) {
        return storeDtoMapper.toCard(storeReader.requireStore(storeId));
    }

    public CrowdStatusResponse crowdStatus(Long storeId) {
        return storeDtoMapper.toCrowd(storeReader.requireStore(storeId));
    }

    public List<PromotionResponse> storePromotions(Long storeId) {
        storeReader.requireStore(storeId);
        return promotions.findByStoreId(storeId).stream()
                .map(promotionDtoMapper::toPromotion)
                .toList();
    }

    private boolean contains(String value, String keyword) {
        return keyword == null || keyword.isBlank() || value.toLowerCase().contains(keyword.toLowerCase());
    }
}
