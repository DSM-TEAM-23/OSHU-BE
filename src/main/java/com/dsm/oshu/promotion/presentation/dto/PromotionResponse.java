package com.dsm.oshu.promotion.presentation.dto;

import java.time.LocalDateTime;

public record PromotionResponse(Long promotionId, Long storeId, String storeName, String type, String title,
                                String content, String imageUrl, LocalDateTime startAt, LocalDateTime endAt,
                                String status) {
}
