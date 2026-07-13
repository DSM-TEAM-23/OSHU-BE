package com.dsm.oshu.application;

import com.dsm.oshu.application.dto.OshuDtos;
import com.dsm.oshu.application.exception.ResourceNotFoundException;
import com.dsm.oshu.domain.store.CrowdLevel;
import com.dsm.oshu.domain.store.Promotion;
import com.dsm.oshu.domain.store.PromotionRepository;
import com.dsm.oshu.domain.store.Store;
import com.dsm.oshu.domain.store.StoreRepository;
import com.dsm.oshu.domain.store.TimeSale;
import com.dsm.oshu.domain.store.TimeSaleRepository;
import com.dsm.oshu.infrastructure.publicdata.PublicStore;
import com.dsm.oshu.infrastructure.publicdata.PublicStoreDataClient;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OshuApplicationService {
    private final StoreRepository stores;
    private final PromotionRepository promotions;
    private final TimeSaleRepository timeSales;
    private final PublicStoreDataClient publicStoreDataClient;

    public OshuApplicationService(StoreRepository stores, PromotionRepository promotions,
                                  TimeSaleRepository timeSales, PublicStoreDataClient publicStoreDataClient) {
        this.stores = stores; this.promotions = promotions; this.timeSales = timeSales;
        this.publicStoreDataClient = publicStoreDataClient;
    }

    public OshuDtos.PageResponse<OshuDtos.StoreCardResponse> listStores(String keyword, String category, int page, int size) {
        List<OshuDtos.StoreCardResponse> all = stores.findAll().stream()
                .filter(store -> contains(store.getName(), keyword) || contains(store.getAddress(), keyword))
                .filter(store -> category == null || category.isBlank() || store.getCategory().equalsIgnoreCase(category))
                .map(this::toCard).toList();
        return page(all, page, size);
    }

    public OshuDtos.StoreDetailResponse getStore(Long storeId) {
        Store store = requireStore(storeId);
        return toDetail(store);
    }

    public List<OshuDtos.StoreCardResponse> mapStores(double latitude, double longitude, int radius, boolean timeSaleOnly) {
        List<PublicStore> publicStores = publicStoreDataClient.findStoresInRadius(latitude, longitude, radius);
        if (!publicStores.isEmpty()) {
            return publicStores.stream().map(store -> new OshuDtos.StoreCardResponse(null, store.name(), store.category(),
                    store.address(), store.latitude(), store.longitude(), null, false, true)).toList();
        }
        return stores.findAll().stream()
                .filter(store -> distanceInMeters(latitude, longitude, store.getLatitude(), store.getLongitude()) <= radius)
                .filter(store -> !timeSaleOnly || hasActiveTimeSale(store.getId()))
                .map(this::toCard).toList();
    }

    public OshuDtos.StoreCardResponse storeSummary(Long storeId) { return toCard(requireStore(storeId)); }

    public OshuDtos.CrowdStatusResponse crowdStatus(Long storeId) { return toCrowd(requireStore(storeId)); }

    public List<OshuDtos.PromotionResponse> storePromotions(Long storeId) {
        requireStore(storeId);
        return promotions.findByStoreId(storeId).stream().map(this::toPromotion).toList();
    }

    public OshuDtos.PageResponse<OshuDtos.PromotionResponse> listPromotions(String status, int page, int size) {
        List<OshuDtos.PromotionResponse> all = promotions.findAll().stream()
                .filter(promotion -> status == null || status.isBlank() || promotion.getStatus().equalsIgnoreCase(status))
                .sorted(Comparator.comparing(Promotion::getEndAt))
                .map(this::toPromotion).toList();
        return page(all, page, size);
    }

    public OshuDtos.PromotionResponse getPromotion(Long promotionId) { return toPromotion(requirePromotion(promotionId)); }

    public List<OshuDtos.StoreCardResponse> ownerStores(String ownerLoginId) {
        return stores.findByOwnerLoginId(ownerLoginId).stream().map(this::toCard).toList();
    }

    @Transactional
    public OshuDtos.StoreDetailResponse createStore(String ownerLoginId, OshuDtos.StoreCreateRequest request) {
        Store store = stores.save(new Store(request.name(), request.category(), request.description(), request.address(),
                request.latitude(), request.longitude(), request.phone(), request.openingHours(), ownerLoginId));
        return toDetail(store);
    }

    public OshuDtos.StoreDetailResponse ownerStore(String ownerLoginId, Long storeId) { return toDetail(requireOwnedStore(ownerLoginId, storeId)); }

    @Transactional
    public OshuDtos.StoreDetailResponse updateStore(String ownerLoginId, Long storeId, OshuDtos.StoreUpdateRequest request) {
        Store store = requireOwnedStore(ownerLoginId, storeId);
        store.update(request.description(), request.phone(), request.openingHours());
        return toDetail(store);
    }

    @Transactional
    public OshuDtos.CrowdStatusResponse updateCrowd(String ownerLoginId, Long storeId, OshuDtos.CrowdStatusRequest request) {
        Store store = requireOwnedStore(ownerLoginId, storeId);
        store.updateCrowd(request.level(), request.estimatedWaitingMinutes());
        return toCrowd(store);
    }

    @Transactional
    public OshuDtos.PromotionResponse createPromotion(String ownerLoginId, Long storeId, OshuDtos.PromotionRequest request) {
        validatePeriod(request.startAt(), request.endAt());
        Store store = requireOwnedStore(ownerLoginId, storeId);
        return toPromotion(promotions.save(new Promotion(store, request.type(), request.title(), request.content(), request.imageUrl(),
                request.startAt(), request.endAt())));
    }

    @Transactional
    public OshuDtos.PromotionResponse updatePromotion(String ownerLoginId, Long promotionId, OshuDtos.PromotionRequest request) {
        validatePeriod(request.startAt(), request.endAt());
        Promotion promotion = requirePromotion(promotionId);
        assertOwner(ownerLoginId, promotion.getStore());
        promotion.update(request.type(), request.title(), request.content(), request.imageUrl(), request.startAt(), request.endAt());
        return toPromotion(promotion);
    }

    @Transactional
    public void deletePromotion(String ownerLoginId, Long promotionId) {
        Promotion promotion = requirePromotion(promotionId);
        assertOwner(ownerLoginId, promotion.getStore());
        promotions.delete(promotion);
    }

    @Transactional
    public OshuDtos.TimeSaleResponse createTimeSale(String ownerLoginId, Long storeId, OshuDtos.TimeSaleRequest request) {
        validateTimeSale(request);
        Store store = requireOwnedStore(ownerLoginId, storeId);
        return toTimeSale(timeSales.save(new TimeSale(store, request.productName(), request.originalPrice(), request.salePrice(),
                request.startAt(), request.endAt(), request.notice())));
    }

    @Transactional
    public OshuDtos.TimeSaleResponse updateTimeSale(String ownerLoginId, Long timeSaleId, OshuDtos.TimeSaleRequest request) {
        validateTimeSale(request);
        TimeSale timeSale = requireTimeSale(timeSaleId);
        assertOwner(ownerLoginId, timeSale.getStore());
        timeSale.update(request.productName(), request.originalPrice(), request.salePrice(), request.startAt(), request.endAt(), request.notice());
        return toTimeSale(timeSale);
    }

    @Transactional
    public OshuDtos.TimeSaleResponse closeTimeSale(String ownerLoginId, Long timeSaleId) {
        TimeSale timeSale = requireTimeSale(timeSaleId);
        assertOwner(ownerLoginId, timeSale.getStore());
        timeSale.close();
        return toTimeSale(timeSale);
    }

    private Store requireStore(Long storeId) {
        return stores.findById(storeId).orElseThrow(() -> new ResourceNotFoundException("가게를 찾을 수 없습니다."));
    }
    private Store requireOwnedStore(String ownerLoginId, Long storeId) {
        Store store = requireStore(storeId); assertOwner(ownerLoginId, store); return store;
    }
    private Promotion requirePromotion(Long promotionId) {
        return promotions.findById(promotionId).orElseThrow(() -> new ResourceNotFoundException("홍보 게시물을 찾을 수 없습니다."));
    }
    private TimeSale requireTimeSale(Long timeSaleId) {
        return timeSales.findById(timeSaleId).orElseThrow(() -> new ResourceNotFoundException("타임세일을 찾을 수 없습니다."));
    }
    private void assertOwner(String ownerLoginId, Store store) {
        if (!store.getOwnerLoginId().equals(ownerLoginId)) throw new IllegalArgumentException("해당 가게를 관리할 권한이 없습니다.");
    }
    private boolean contains(String value, String keyword) {
        return keyword == null || keyword.isBlank() || value.toLowerCase().contains(keyword.toLowerCase());
    }
    private boolean hasActiveTimeSale(Long storeId) {
        LocalDateTime now = LocalDateTime.now();
        return timeSales.findByStoreId(storeId).stream().anyMatch(sale -> sale.getStatus().equals("SCHEDULED")
                && !now.isBefore(sale.getStartAt()) && !now.isAfter(sale.getEndAt()));
    }
    private void validatePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (!endAt.isAfter(startAt)) throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
    }
    private void validateTimeSale(OshuDtos.TimeSaleRequest request) {
        validatePeriod(request.startAt(), request.endAt());
        if (request.salePrice() >= request.originalPrice()) throw new IllegalArgumentException("할인가는 정상가보다 낮아야 합니다.");
    }
    private OshuDtos.StoreCardResponse toCard(Store store) {
        return new OshuDtos.StoreCardResponse(store.getId(), store.getName(), store.getCategory(), store.getAddress(),
                store.getLatitude(), store.getLongitude(), store.getCrowdLevel().name(), hasActiveTimeSale(store.getId()), false);
    }
    private OshuDtos.StoreDetailResponse toDetail(Store store) {
        return new OshuDtos.StoreDetailResponse(store.getId(), store.getName(), store.getCategory(), store.getDescription(),
                store.getAddress(), store.getLatitude(), store.getLongitude(), store.getPhone(), store.getOpeningHours(),
                toCrowd(store), storePromotions(store.getId()), timeSales.findByStoreId(store.getId()).stream().map(this::toTimeSale).toList());
    }
    private OshuDtos.CrowdStatusResponse toCrowd(Store store) {
        return new OshuDtos.CrowdStatusResponse(store.getCrowdLevel().name(), store.getCrowdLevel().getLabel(), store.getEstimatedWaitingMinutes());
    }
    private OshuDtos.PromotionResponse toPromotion(Promotion promotion) {
        return new OshuDtos.PromotionResponse(promotion.getId(), promotion.getStore().getId(), promotion.getStore().getName(),
                promotion.getType(), promotion.getTitle(), promotion.getContent(), promotion.getImageUrl(), promotion.getStartAt(),
                promotion.getEndAt(), promotion.getStatus());
    }
    private OshuDtos.TimeSaleResponse toTimeSale(TimeSale sale) {
        return new OshuDtos.TimeSaleResponse(sale.getId(), sale.getStore().getId(), sale.getProductName(), sale.getOriginalPrice(),
                sale.getSalePrice(), sale.getStartAt(), sale.getEndAt(), sale.getNotice(), sale.getStatus());
    }
    private static double distanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6_371_000;
        double dLat = Math.toRadians(lat2 - lat1); double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
    private static <T> OshuDtos.PageResponse<T> page(List<T> source, int page, int size) {
        int normalizedPage = Math.max(page, 0); int normalizedSize = Math.min(Math.max(size, 1), 100);
        int from = Math.min(normalizedPage * normalizedSize, source.size());
        int to = Math.min(from + normalizedSize, source.size());
        return new OshuDtos.PageResponse<>(source.subList(from, to), normalizedPage, normalizedSize, source.size());
    }
}
