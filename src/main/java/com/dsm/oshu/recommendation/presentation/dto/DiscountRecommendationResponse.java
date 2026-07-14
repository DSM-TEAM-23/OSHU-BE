package com.dsm.oshu.recommendation.presentation.dto;

import java.time.LocalDate;

public record DiscountRecommendationResponse(
        String recommendedDay,
        int startHour,
        int endHour,
        int discountRate,
        String reason,
        LocalDate analysisStartDate,
        LocalDate analysisEndDate,
        int analyzedDays
) {
}
