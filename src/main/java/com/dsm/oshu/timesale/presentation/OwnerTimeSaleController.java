package com.dsm.oshu.timesale.presentation;

import com.dsm.oshu.timesale.presentation.dto.TimeSaleRequest;
import com.dsm.oshu.timesale.presentation.dto.TimeSaleResponse;
import com.dsm.oshu.timesale.service.OwnerTimeSaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Owner Time Sales", description = "점주 타임세일 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/owner")
public class OwnerTimeSaleController {
    private final OwnerTimeSaleService ownerTimeSaleService;

    public OwnerTimeSaleController(OwnerTimeSaleService ownerTimeSaleService) {
        this.ownerTimeSaleService = ownerTimeSaleService;
    }

    @Operation(summary = "내 가게 타임세일 등록")
    @PostMapping("/stores/{storeId}/time-sales")
    @ResponseStatus(HttpStatus.CREATED)
    public TimeSaleResponse createTimeSale(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long storeId,
                                           @Valid @RequestBody TimeSaleRequest request) {
        return ownerTimeSaleService.createTimeSale(ownerLoginId, storeId, request);
    }

    @Operation(summary = "내 가게 타임세일 수정")
    @PatchMapping("/time-sales/{timeSaleId}")
    public TimeSaleResponse updateTimeSale(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long timeSaleId,
                                           @Valid @RequestBody TimeSaleRequest request) {
        return ownerTimeSaleService.updateTimeSale(ownerLoginId, timeSaleId, request);
    }

    @Operation(summary = "내 가게 타임세일 종료")
    @PatchMapping("/time-sales/{timeSaleId}/close")
    public TimeSaleResponse closeTimeSale(@AuthenticationPrincipal String ownerLoginId, @PathVariable Long timeSaleId) {
        return ownerTimeSaleService.closeTimeSale(ownerLoginId, timeSaleId);
    }
}
