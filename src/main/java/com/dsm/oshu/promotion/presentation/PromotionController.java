package com.dsm.oshu.promotion.presentation;

import com.dsm.oshu.promotion.service.PromotionQueryService;
import com.dsm.oshu.common.presentation.dto.PageResponse;
import com.dsm.oshu.promotion.presentation.dto.PromotionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Promotions", description = "홍보 포스터와 행사 조회")
@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionController {
    private final PromotionQueryService promotionQueryService;
    public PromotionController(PromotionQueryService promotionQueryService) { this.promotionQueryService = promotionQueryService; }

    @Operation(summary = "홍보 포스터 목록 조회")
    @GetMapping
    public PageResponse<PromotionResponse> list(@RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return promotionQueryService.listPromotions(status, page, size);
    }

    @Operation(summary = "행사 상세 조회")
    @GetMapping("/{promotionId}")
    public PromotionResponse detail(@PathVariable Long promotionId) { return promotionQueryService.getPromotion(promotionId); }
}
