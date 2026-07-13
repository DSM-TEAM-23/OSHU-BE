package com.dsm.oshu.promotion.service;

import com.dsm.oshu.promotion.presentation.dto.PromotionRequest;
import com.dsm.oshu.promotion.presentation.dto.PromotionResponse;
import com.dsm.oshu.common.service.PeriodValidator;
import com.dsm.oshu.promotion.service.PromotionDtoMapper;
import com.dsm.oshu.promotion.service.PromotionReader;
import com.dsm.oshu.store.service.StoreReader;
import com.dsm.oshu.promotion.domain.Promotion;
import com.dsm.oshu.promotion.domain.PromotionRepository;
import com.dsm.oshu.store.domain.Store;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OwnerPromotionService {
    private final PromotionRepository promotions;
    private final StoreReader storeReader;
    private final PromotionReader promotionReader;
    private final PromotionDtoMapper promotionDtoMapper;
    private final PeriodValidator periodValidator;

    public OwnerPromotionService(PromotionRepository promotions, StoreReader storeReader,
                                 PromotionReader promotionReader, PromotionDtoMapper promotionDtoMapper,
                                 PeriodValidator periodValidator) {
        this.promotions = promotions;
        this.storeReader = storeReader;
        this.promotionReader = promotionReader;
        this.promotionDtoMapper = promotionDtoMapper;
        this.periodValidator = periodValidator;
    }

    @Transactional
    public PromotionResponse createPromotion(String ownerLoginId, Long storeId, PromotionRequest request) {
        periodValidator.validatePeriod(request.startAt(), request.endAt());
        Store store = storeReader.requireOwnedStore(ownerLoginId, storeId);
        Promotion promotion = promotions.save(new Promotion(
                store,
                request.type(),
                request.title(),
                request.content(),
                request.imageUrl(),
                request.startAt(),
                request.endAt()));
        return promotionDtoMapper.toPromotion(promotion);
    }

    @Transactional
    public PromotionResponse updatePromotion(String ownerLoginId, Long promotionId,
                                                      PromotionRequest request) {
        periodValidator.validatePeriod(request.startAt(), request.endAt());
        Promotion promotion = promotionReader.requirePromotion(promotionId);
        storeReader.assertOwner(ownerLoginId, promotion.getStore());
        promotion.update(request.type(), request.title(), request.content(), request.imageUrl(),
                request.startAt(), request.endAt());
        return promotionDtoMapper.toPromotion(promotion);
    }

    @Transactional
    public void deletePromotion(String ownerLoginId, Long promotionId) {
        Promotion promotion = promotionReader.requirePromotion(promotionId);
        storeReader.assertOwner(ownerLoginId, promotion.getStore());
        promotions.delete(promotion);
    }
}
