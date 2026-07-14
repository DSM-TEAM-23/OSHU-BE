package com.dsm.oshu.inquiry.service;

import com.dsm.oshu.inquiry.domain.Inquiry;
import com.dsm.oshu.inquiry.domain.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryDeleteService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    public void execute(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        inquiryRepository.delete(inquiry);
    }
}
