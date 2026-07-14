package com.dsm.oshu.inquiry.service;

import com.dsm.oshu.inquiry.domain.Inquiry;
import com.dsm.oshu.inquiry.domain.repository.InquiryRepository;
import com.dsm.oshu.inquiry.presentation.dto.request.InquiryRequest;
import com.dsm.oshu.store.domain.Store;
import com.dsm.oshu.store.service.StoreReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryCreateService {
    private final InquiryRepository inquiryRepository;
    private final StoreReader storeReader;

    @Transactional
    public void create(Long storeId, InquiryRequest request) {
        Store store = storeReader.requireStore(storeId);
        Inquiry inquiry = Inquiry.builder()
                .store(store)
                .title(request.getTitle())
                .content(request.getContent())
                .name(request.getName())
                .number(request.getNumber())
                .build();

        inquiryRepository.save(inquiry);
    }
}
