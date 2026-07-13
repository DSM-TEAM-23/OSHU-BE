package com.dsm.oshu.promotion.presentation;

import com.dsm.oshu.promotion.presentation.dto.PromotionRequest;
import com.dsm.oshu.promotion.presentation.dto.PromotionResponse;
import com.dsm.oshu.promotion.service.OwnerPromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Owner Promotions", description = "점주 홍보 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/owner")
public class OwnerPromotionController {
    private final OwnerPromotionService ownerPromotionService;

    public OwnerPromotionController(OwnerPromotionService ownerPromotionService) {
        this.ownerPromotionService = ownerPromotionService;
    }

    @Operation(summary = "내 가게 홍보 등록")
    @PostMapping("/stores/{storeId}/promotions")
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionResponse createPromotion(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId,
                                             @Valid @RequestBody PromotionRequest request) {
        return ownerPromotionService.createPromotion(ownerLoginId, storeId, request);
    }

    @Operation(summary = "내 가게 홍보 수정")
    @PatchMapping("/promotions/{promotionId}")
    public PromotionResponse updatePromotion(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long promotionId,
                                             @Valid @RequestBody PromotionRequest request) {
        return ownerPromotionService.updatePromotion(ownerLoginId, promotionId, request);
    }

    @Operation(summary = "내 가게 홍보 삭제")
    @DeleteMapping("/promotions/{promotionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePromotion(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long promotionId) {
        ownerPromotionService.deletePromotion(ownerLoginId, promotionId);
    }
}
