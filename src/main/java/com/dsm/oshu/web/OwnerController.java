package com.dsm.oshu.web;

import com.dsm.oshu.application.OshuApplicationService;
import com.dsm.oshu.application.dto.OshuDtos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Owner", description = "점주 가게·홍보·타임세일 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/owner")
public class OwnerController {
    private final OshuApplicationService oshuService;
    public OwnerController(OshuApplicationService oshuService) { this.oshuService = oshuService; }

    @Operation(summary = "내 가게 등록")
    @PostMapping("/stores") @ResponseStatus(HttpStatus.CREATED)
    public OshuDtos.StoreDetailResponse createStore(@AuthenticationPrincipal String ownerLoginId,
            @Valid @RequestBody OshuDtos.StoreCreateRequest request) { return oshuService.createStore(ownerLoginId, request); }

    @Operation(summary = "내 가게 목록 조회")
    @GetMapping("/stores")
    public List<OshuDtos.StoreCardResponse> myStores(@AuthenticationPrincipal String ownerLoginId) { return oshuService.ownerStores(ownerLoginId); }

    @Operation(summary = "내 가게 상세 조회")
    @GetMapping("/stores/{storeId}")
    public OshuDtos.StoreDetailResponse myStore(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId) {
        return oshuService.ownerStore(ownerLoginId, storeId);
    }

    @Operation(summary = "내 가게 정보 수정")
    @PatchMapping("/stores/{storeId}")
    public OshuDtos.StoreDetailResponse updateStore(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId,
            @RequestBody OshuDtos.StoreUpdateRequest request) { return oshuService.updateStore(ownerLoginId, storeId, request); }

    @Operation(summary = "혼잡도 갱신")
    @PatchMapping("/stores/{storeId}/crowd-status")
    public OshuDtos.CrowdStatusResponse updateCrowd(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId,
            @Valid @RequestBody OshuDtos.CrowdStatusRequest request) { return oshuService.updateCrowd(ownerLoginId, storeId, request); }

    @Operation(summary = "내 가게 홍보 등록")
    @PostMapping("/stores/{storeId}/promotions") @ResponseStatus(HttpStatus.CREATED)
    public OshuDtos.PromotionResponse createPromotion(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId,
            @Valid @RequestBody OshuDtos.PromotionRequest request) { return oshuService.createPromotion(ownerLoginId, storeId, request); }

    @Operation(summary = "내 가게 홍보 수정")
    @PatchMapping("/promotions/{promotionId}")
    public OshuDtos.PromotionResponse updatePromotion(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long promotionId,
            @Valid @RequestBody OshuDtos.PromotionRequest request) { return oshuService.updatePromotion(ownerLoginId, promotionId, request); }

    @Operation(summary = "내 가게 홍보 삭제")
    @DeleteMapping("/promotions/{promotionId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePromotion(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long promotionId) {
        oshuService.deletePromotion(ownerLoginId, promotionId);
    }

    @Operation(summary = "내 가게 타임세일 등록")
    @PostMapping("/stores/{storeId}/time-sales") @ResponseStatus(HttpStatus.CREATED)
    public OshuDtos.TimeSaleResponse createTimeSale(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId,
            @Valid @RequestBody OshuDtos.TimeSaleRequest request) { return oshuService.createTimeSale(ownerLoginId, storeId, request); }

    @Operation(summary = "내 가게 타임세일 수정")
    @PatchMapping("/time-sales/{timeSaleId}")
    public OshuDtos.TimeSaleResponse updateTimeSale(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long timeSaleId,
            @Valid @RequestBody OshuDtos.TimeSaleRequest request) { return oshuService.updateTimeSale(ownerLoginId, timeSaleId, request); }

    @Operation(summary = "내 가게 타임세일 종료")
    @PatchMapping("/time-sales/{timeSaleId}/close")
    public OshuDtos.TimeSaleResponse closeTimeSale(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long timeSaleId) {
        return oshuService.closeTimeSale(ownerLoginId, timeSaleId);
    }
}
