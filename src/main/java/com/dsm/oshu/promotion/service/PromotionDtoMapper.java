package com.dsm.oshu.promotion.service;

import com.dsm.oshu.promotion.presentation.dto.PromotionResponse;
import com.dsm.oshu.promotion.domain.Promotion;
import org.springframework.stereotype.Component;

@Component
public class PromotionDtoMapper {
    public PromotionResponse toPromotion(Promotion promotion) {
        return new PromotionResponse(
                promotion.getId(),
                promotion.getStore().getId(),
                promotion.getStore().getName(),
                promotion.getType(),
                promotion.getTitle(),
                promotion.getContent(),
                promotion.getImageUrl(),
                promotion.getStartAt(),
                promotion.getEndAt(),
                promotion.getStatus());
    }
}
