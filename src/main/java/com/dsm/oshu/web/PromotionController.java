package com.dsm.oshu.web;

import com.dsm.oshu.application.OshuApplicationService;
import com.dsm.oshu.application.dto.OshuDtos;
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
    private final OshuApplicationService oshuService;
    public PromotionController(OshuApplicationService oshuService) { this.oshuService = oshuService; }

    @Operation(summary = "홍보 포스터 목록 조회")
    @GetMapping
    public OshuDtos.PageResponse<OshuDtos.PromotionResponse> list(@RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return oshuService.listPromotions(status, page, size);
    }

    @Operation(summary = "행사 상세 조회")
    @GetMapping("/{promotionId}")
    public OshuDtos.PromotionResponse detail(@PathVariable Long promotionId) { return oshuService.getPromotion(promotionId); }
}
