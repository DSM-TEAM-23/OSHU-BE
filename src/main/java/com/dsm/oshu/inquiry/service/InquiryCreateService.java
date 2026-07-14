package com.dsm.oshu.inquiry.service;

import com.dsm.oshu.inquiry.domain.Inquiry;
import com.dsm.oshu.inquiry.domain.repository.InquiryRepository;
import com.dsm.oshu.inquiry.presentation.dto.request.InquiryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryCreateService {
    private final InquiryRepository inquiryRepository;

    @Transactional
    public void create(InquiryRequest request) {
        Inquiry inquiry = Inquiry.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .name(request.getName())
                .number(request.getNumber())
                .build();

        inquiryRepository.save(inquiry);
    }
}
