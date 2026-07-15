package com.dsm.oshu.store.service;

import com.dsm.oshu.store.presentation.dto.CrowdStatusResponse;
import com.dsm.oshu.store.presentation.dto.MapStoreResponse;
import com.dsm.oshu.store.presentation.dto.StoreCardResponse;
import com.dsm.oshu.store.presentation.dto.StoreDetailResponse;
import com.dsm.oshu.promotion.service.PromotionDtoMapper;
import com.dsm.oshu.timesale.service.TimeSaleDtoMapper;
import com.dsm.oshu.promotion.domain.PromotionRepository;
import com.dsm.oshu.store.domain.Store;
import com.dsm.oshu.timesale.domain.TimeSaleRepository;
import com.dsm.oshu.timesale.domain.TimeSale;
import java.time.LocalDateTime;
import java.util.Comparator;
import org.springframework.stereotype.Component;

@Component
public class StoreDtoMapper {
    private final PromotionRepository promotions;
    private final TimeSaleRepository timeSales;
    private final PromotionDtoMapper promotionDtoMapper;
    private final TimeSaleDtoMapper timeSaleDtoMapper;

    public StoreDtoMapper(PromotionRepository promotions, TimeSaleRepository timeSales,
                          PromotionDtoMapper promotionDtoMapper, TimeSaleDtoMapper timeSaleDtoMapper) {
        this.promotions = promotions;
        this.timeSales = timeSales;
        this.promotionDtoMapper = promotionDtoMapper;
        this.timeSaleDtoMapper = timeSaleDtoMapper;
    }

    public StoreCardResponse toCard(Store store) {
        return new StoreCardResponse(
                store.getId(),
                store.getName(),
                categoryLabel(store),
                store.getAddress(),
                store.getLatitude(),
                store.getLongitude(),
                store.getCrowdLevel().name(),
                store.getOpeningHours(),
                hasActiveTimeSale(store.getId()),
                false);
    }

    public StoreDetailResponse toDetail(Store store) {
        return new StoreDetailResponse(
                store.getId(),
                store.getName(),
                categoryLabel(store),
                store.getDescription(),
                store.getAddress(),
                store.getLatitude(),
                store.getLongitude(),
                store.getPhone(),
                store.getOpeningHours(),
                toCrowd(store),
                promotions.findByStoreId(store.getId()).stream()
                        .map(promotionDtoMapper::toPromotion)
                        .toList(),
                timeSales.findByStoreId(store.getId()).stream()
                        .map(timeSaleDtoMapper::toTimeSale)
                        .toList());
    }

    public MapStoreResponse toMap(Store store) {
        LocalDateTime now = LocalDateTime.now();
        TimeSale timeSale = findActiveOrUpcomingTimeSale(store.getId(), now);
        return new MapStoreResponse(
                store.getId(),
                store.getName(),
                categoryLabel(store),
                store.getAddress(),
                store.getLatitude(),
                store.getLongitude(),
                store.getCrowdLevel().name(),
                store.getOpeningHours(),
                timeSale != null && isActive(timeSale, now),
                false,
                timeSale == null ? null : timeSale.getStartAt(),
                timeSale == null ? null : timeSale.getEndAt(),
                timeSale == null ? null : discountRate(timeSale));
    }

    public CrowdStatusResponse toCrowd(Store store) {
        return new CrowdStatusResponse(
                store.getCrowdLevel().name(),
                store.getCrowdLevel().getLabel(),
                store.getEstimatedWaitingMinutes());
    }

    public boolean hasActiveTimeSale(Long storeId) {
        LocalDateTime now = LocalDateTime.now();
        return timeSales.findByStoreId(storeId).stream().anyMatch(sale -> isActive(sale, now));
    }

    private TimeSale findActiveOrUpcomingTimeSale(Long storeId, LocalDateTime now) {
        return timeSales.findByStoreId(storeId).stream()
                .filter(sale -> sale.getStatus().equals("SCHEDULED")
                        && !now.isAfter(sale.getEndAt()))
                .min(Comparator.comparing((TimeSale sale) -> now.isBefore(sale.getStartAt()))
                        .thenComparing(TimeSale::getStartAt))
                .orElse(null);
    }

    private boolean isActive(TimeSale sale, LocalDateTime now) {
        return sale.getStatus().equals("SCHEDULED")
                && !now.isBefore(sale.getStartAt())
                && !now.isAfter(sale.getEndAt());
    }

    private int discountRate(TimeSale timeSale) {
        return (int) Math.round((timeSale.getOriginalPrice() - timeSale.getSalePrice())
                * 100.0 / timeSale.getOriginalPrice());
    }

    private String categoryLabel(Store store) {
        if (store.getCategory() == com.dsm.oshu.store.domain.Category.OTHER
                && store.getCustomCategory() != null
                && !store.getCustomCategory().isBlank()) {
            return store.getCustomCategory();
        }
        return store.getCategory().getLabel();
    }
}
