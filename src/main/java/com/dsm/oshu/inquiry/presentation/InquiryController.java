package com.dsm.oshu.inquiry.presentation;

import com.dsm.oshu.inquiry.presentation.dto.request.InquiryRequest;
import com.dsm.oshu.inquiry.presentation.dto.response.InquiryResponse;
import com.dsm.oshu.inquiry.service.InquiryAllQueryService;
import com.dsm.oshu.inquiry.service.InquiryCreateService;
import com.dsm.oshu.inquiry.service.InquiryDeleteService;
import com.dsm.oshu.inquiry.service.InquiryQueryService;
import com.dsm.oshu.inquiry.service.InquiryUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inquiries", description = "가게 문의")
@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController {

    private final InquiryCreateService inquiryCreateService;
    private final InquiryAllQueryService inquiryAllQueryService;
    private final InquiryQueryService inquiryQueryService;
    private final InquiryUpdateService inquiryUpdateService;
    private final InquiryDeleteService inquiryDeleteService;

    @Operation(summary = "가게 문의 등록")
    @PostMapping("/store/{storeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createInquiry(@PathVariable Long storeId, @Valid @RequestBody InquiryRequest request){
        inquiryCreateService.create(storeId, request);
    }

    @Operation(summary = "내 가게 문의 목록 조회")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/store/{storeId}")
    public List<InquiryResponse> getStoreInquiries(
            @AuthenticationPrincipal String ownerLoginId,
            @PathVariable Long storeId
    ) {
        return inquiryAllQueryService.execute(ownerLoginId, storeId);
    }

    @Operation(summary = "내 가게 문의 상세 조회")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{inquiryId}")
    public InquiryResponse getInquiry(
            @AuthenticationPrincipal String ownerLoginId,
            @PathVariable Long inquiryId
    ) {
        return inquiryQueryService.execute(ownerLoginId, inquiryId);
    }

    @Operation(summary = "내 가게 문의 수정")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{inquiryId}")
    public void updateInquiry(
            @AuthenticationPrincipal String ownerLoginId,
            @PathVariable Long inquiryId,
            @Valid @RequestBody InquiryRequest request
    ) {
        inquiryUpdateService.execute(ownerLoginId, inquiryId, request);
    }

    @Operation(summary = "내 가게 문의 삭제")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{inquiryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInquiry(
            @AuthenticationPrincipal String ownerLoginId,
            @PathVariable Long inquiryId
    ) {
        inquiryDeleteService.execute(ownerLoginId, inquiryId);
    }
}
