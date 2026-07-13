package com.dsm.oshu.promotion.service;

import com.dsm.oshu.common.presentation.dto.PageResponse;
import com.dsm.oshu.promotion.presentation.dto.PromotionResponse;
import com.dsm.oshu.common.service.PageUtils;
import com.dsm.oshu.promotion.service.PromotionDtoMapper;
import com.dsm.oshu.promotion.service.PromotionReader;
import com.dsm.oshu.promotion.domain.Promotion;
import com.dsm.oshu.promotion.domain.PromotionRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PromotionQueryService {
    private final PromotionRepository promotions;
    private final PromotionReader promotionReader;
    private final PromotionDtoMapper promotionDtoMapper;

    public PromotionQueryService(PromotionRepository promotions, PromotionReader promotionReader,
                                 PromotionDtoMapper promotionDtoMapper) {
        this.promotions = promotions;
        this.promotionReader = promotionReader;
        this.promotionDtoMapper = promotionDtoMapper;
    }

    public PageResponse<PromotionResponse> listPromotions(String status, int page, int size) {
        List<PromotionResponse> all = promotions.findAll().stream()
                .filter(promotion -> status == null || status.isBlank() || promotion.getStatus().equalsIgnoreCase(status))
                .sorted(Comparator.comparing(Promotion::getEndAt))
                .map(promotionDtoMapper::toPromotion)
                .toList();
        return PageUtils.page(all, page, size);
    }

    public PromotionResponse getPromotion(Long promotionId) {
        return promotionDtoMapper.toPromotion(promotionReader.requirePromotion(promotionId));
    }
}
