package com.dsm.oshu.recommendation.presentation;

import com.dsm.oshu.recommendation.presentation.dto.DailyOrderStatisticsRequest;
import com.dsm.oshu.recommendation.presentation.dto.DiscountRecommendationResponse;
import com.dsm.oshu.recommendation.service.DiscountRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Owner Discount Recommendations", description = "AI 할인 시간대 추천")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/owner/stores/{storeId}")
public class OwnerDiscountRecommendationController {
    private final DiscountRecommendationService discountRecommendationService;

    public OwnerDiscountRecommendationController(DiscountRecommendationService discountRecommendationService) {
        this.discountRecommendationService = discountRecommendationService;
    }

    @Operation(summary = "하루 시간대별 주문량 저장")
    @PostMapping("/order-statistics")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveDailyOrderStatistics(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId,
                                         @Valid @RequestBody DailyOrderStatisticsRequest request) {
        discountRecommendationService.saveDailyStatistics(ownerLoginId, storeId, request);
    }

    @Operation(summary = "AI 할인 시간대 추천")
    @GetMapping("/discount-recommendations")
    public DiscountRecommendationResponse recommend(@AuthenticationPrincipal String ownerLoginId,
                                                    @PathVariable Long storeId) {
        return discountRecommendationService.recommend(ownerLoginId, storeId);
    }
}
