package com.dsm.oshu.inquiry.service;

import com.dsm.oshu.inquiry.domain.repository.InquiryRepository;
import com.dsm.oshu.inquiry.presentation.dto.response.InquiryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryAllQueryService {

    private InquiryRepository inquiryRepository;

    @Transactional(readOnly = true)
    public List<InquiryResponse> findAll(){
        return inquiryRepository.findAll()
                .stream()
                .map(InquiryResponse::new)
                .toList();
    }
}
