package com.dsm.oshu.store.service;

import com.dsm.oshu.store.presentation.dto.CrowdStatusResponse;
import com.dsm.oshu.store.presentation.dto.StoreCardResponse;
import com.dsm.oshu.store.presentation.dto.StoreDetailResponse;
import com.dsm.oshu.promotion.service.PromotionDtoMapper;
import com.dsm.oshu.timesale.service.TimeSaleDtoMapper;
import com.dsm.oshu.promotion.domain.PromotionRepository;
import com.dsm.oshu.store.domain.Store;
import com.dsm.oshu.timesale.domain.TimeSaleRepository;
import java.time.LocalDateTime;
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

    public CrowdStatusResponse toCrowd(Store store) {
        return new CrowdStatusResponse(
                store.getCrowdLevel().name(),
                store.getCrowdLevel().getLabel(),
                store.getEstimatedWaitingMinutes());
    }

    public boolean hasActiveTimeSale(Long storeId) {
        LocalDateTime now = LocalDateTime.now();
        return timeSales.findByStoreId(storeId).stream()
                .anyMatch(sale -> sale.getStatus().equals("SCHEDULED")
                        && !now.isBefore(sale.getStartAt())
                        && !now.isAfter(sale.getEndAt()));
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
