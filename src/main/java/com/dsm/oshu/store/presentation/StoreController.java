package com.dsm.oshu.store.presentation;

import com.dsm.oshu.store.service.StoreQueryService;
import com.dsm.oshu.common.presentation.dto.PageResponse;
import com.dsm.oshu.promotion.presentation.dto.PromotionResponse;
import com.dsm.oshu.store.presentation.dto.CrowdStatusResponse;
import com.dsm.oshu.store.presentation.dto.StoreCardResponse;
import com.dsm.oshu.store.presentation.dto.StoreDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stores", description = "소비자 가게·지도 조회")
@RestController
@RequestMapping("/stores")
public class StoreController {
    private final StoreQueryService storeQueryService;
    public StoreController(StoreQueryService storeQueryService) { this.storeQueryService = storeQueryService; }

    @Operation(summary = "가게 목록 조회")
    @GetMapping
    public PageResponse<StoreCardResponse> list(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) { return storeQueryService.listStores(keyword, category, page, size); }

    @Operation(summary = "지도 메인", description = "공공데이터 키가 설정되면 반경 내 공공 상가 정보를 우선 반환합니다.")
    @GetMapping("/map")
    public List<StoreCardResponse> map(@RequestParam double latitude, @RequestParam double longitude,
            @RequestParam(defaultValue = "1500") int radius, @RequestParam(defaultValue = "false") boolean timeSaleOnly) {
        return storeQueryService.mapStores(latitude, longitude, radius, timeSaleOnly);
    }

    @Operation(summary = "가게 상세 조회")
    @GetMapping("/{storeId}")
    public StoreDetailResponse detail(@PathVariable Long storeId) { return storeQueryService.getStore(storeId); }

    @Operation(summary = "지도 가게 선택 요약")
    @GetMapping("/{storeId}/summary")
    public StoreCardResponse summary(@PathVariable Long storeId) { return storeQueryService.storeSummary(storeId); }

    @Operation(summary = "가게 혼잡도 조회")
    @GetMapping("/{storeId}/crowd-status")
    public CrowdStatusResponse crowdStatus(@PathVariable Long storeId) { return storeQueryService.crowdStatus(storeId); }

    @Operation(summary = "가게 행사 조회")
    @GetMapping("/{storeId}/promotions")
    public List<PromotionResponse> promotions(@PathVariable Long storeId) { return storeQueryService.storePromotions(storeId); }
}
