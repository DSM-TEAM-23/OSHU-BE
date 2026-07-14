package com.dsm.oshu.recommendation.service;

public record AiDiscountRecommendation(
        String recommendedDay,
        int startHour,
        int endHour,
        int discountRate,
        String reason
) {
}
