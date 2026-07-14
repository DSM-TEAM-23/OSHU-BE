package com.dsm.oshu.inquiry.presentation;

import com.dsm.oshu.inquiry.presentation.dto.request.InquiryRequest;
import com.dsm.oshu.inquiry.presentation.dto.response.InquiryResponse;
import com.dsm.oshu.inquiry.service.InquiryCreateService;
import com.dsm.oshu.inquiry.service.InquiryDeleteService;
import com.dsm.oshu.inquiry.service.InquiryQueryService;
import com.dsm.oshu.inquiry.service.InquiryUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryCreateService inquiryCreateService;
    private final InquiryGetService inquiryGetService;
    private final InquiryQueryService inquiryQueryService;
    private final InquiryUpdateService inquiryUpdateService;
    private final InquiryDeleteService inquiryDeleteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createInquiry(@RequestBody InquiryRequest request){
        inquiryCreateService.create(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InquiryResponse getInquiry(@PathVariable Long id){
        return inquiryQueryService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InquiryResponse updateInquiry(
            @PathVariable Long id,
            @RequestBody InquiryRequest request
    ) {
        return inquiryUpdateService.execute(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInquiry(@PathVariable Long id) {
        inquiryDeleteService.execute(id);
    }
}
