package com.dsm.oshu.store.presentation;

import com.dsm.oshu.store.presentation.dto.CrowdStatusRequest;
import com.dsm.oshu.store.presentation.dto.CrowdStatusResponse;
import com.dsm.oshu.store.presentation.dto.StoreCardResponse;
import com.dsm.oshu.store.presentation.dto.StoreCreateRequest;
import com.dsm.oshu.store.presentation.dto.StoreDetailResponse;
import com.dsm.oshu.store.presentation.dto.StoreUpdateRequest;
import com.dsm.oshu.store.service.OwnerStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Owner Stores", description = "점주 가게 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/owner/stores")
public class OwnerStoreController {
    private final OwnerStoreService ownerStoreService;

    public OwnerStoreController(OwnerStoreService ownerStoreService) {
        this.ownerStoreService = ownerStoreService;
    }

    @Operation(summary = "내 가게 등록", description = "openingHours에는 영업 시작·마감 시간을 `09:00 - 21:00` 형식으로 입력합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreDetailResponse createStore(@AuthenticationPrincipal String ownerLoginId,
                                           @Valid @RequestBody StoreCreateRequest request) {
        return ownerStoreService.createStore(ownerLoginId, request);
    }

    @Operation(summary = "내 가게 목록 조회")
    @GetMapping
    public List<StoreCardResponse> myStores(@AuthenticationPrincipal String ownerLoginId) {
        return ownerStoreService.ownerStores(ownerLoginId);
    }

    @Operation(summary = "내 가게 상세 조회")
    @GetMapping("/{storeId}")
    public StoreDetailResponse myStore(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId) {
        return ownerStoreService.ownerStore(ownerLoginId, storeId);
    }

    @Operation(summary = "내 가게 정보 수정", description = "openingHours에는 영업 시작·마감 시간을 `09:00 - 21:00` 형식으로 입력합니다.")
    @PatchMapping("/{storeId}")
    public StoreDetailResponse updateStore(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId,
                                           @Valid @RequestBody StoreUpdateRequest request) {
        return ownerStoreService.updateStore(ownerLoginId, storeId, request);
    }

    @Operation(summary = "혼잡도 갱신")
    @PatchMapping("/{storeId}/crowd-status")
    public CrowdStatusResponse updateCrowd(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId,
                                           @Valid @RequestBody CrowdStatusRequest request) {
        return ownerStoreService.updateCrowd(ownerLoginId, storeId, request);
    }
}
