package com.dsm.oshu.promotion.service;

import com.dsm.oshu.promotion.domain.Promotion;
import com.dsm.oshu.promotion.domain.PromotionRepository;
import com.dsm.oshu.promotion.exception.PromotionNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class PromotionReader {
    private final PromotionRepository promotions;

    public PromotionReader(PromotionRepository promotions) {
        this.promotions = promotions;
    }

    public Promotion requirePromotion(Long promotionId) {
        return promotions.findById(promotionId)
                .orElseThrow(() -> new PromotionNotFoundException());
    }
}
