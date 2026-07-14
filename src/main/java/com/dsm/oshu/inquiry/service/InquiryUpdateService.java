package com.dsm.oshu.inquiry.service;


import com.dsm.oshu.inquiry.domain.Inquiry;
import com.dsm.oshu.inquiry.domain.repository.InquiryRepository;
import com.dsm.oshu.inquiry.presentation.dto.request.InquiryRequest;
import com.dsm.oshu.inquiry.presentation.dto.response.InquiryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryUpdateService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    public InquiryResponse execute(Long id, InquiryRequest request) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        inquiry.update(request.getTitle(), request.getContent(), request.getName(), request.getNumber());

        return new InquiryResponse(inquiry);
    }
}
