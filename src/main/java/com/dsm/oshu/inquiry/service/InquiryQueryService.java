package com.dsm.oshu.inquiry.service;

import com.dsm.oshu.inquiry.domain.Inquiry;
import com.dsm.oshu.inquiry.domain.repository.InquiryRepository;
import com.dsm.oshu.inquiry.presentation.dto.response.InquiryResponse;
import com.dsm.oshu.store.service.StoreReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryQueryService {

    private final InquiryRepository inquiryRepository;
    private final StoreReader storeReader;

    @Transactional(readOnly = true)
    public InquiryResponse execute(String ownerLoginId, Long inquiryId){
        Inquiry inquiry = findInquiry(inquiryId);
        storeReader.assertOwner(ownerLoginId, inquiry.getStore());

        return new InquiryResponse(inquiry);
    }

    private Inquiry findInquiry(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다. id=" + inquiryId));
    }
}
