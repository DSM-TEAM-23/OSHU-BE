package com.dsm.oshu.inquiry.service;

import com.dsm.oshu.inquiry.domain.repository.InquiryRepository;
import com.dsm.oshu.inquiry.presentation.dto.response.InquiryResponse;
import com.dsm.oshu.store.service.StoreReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryAllQueryService {

    private final InquiryRepository inquiryRepository;
    private final StoreReader storeReader;

    @Transactional(readOnly = true)
    public List<InquiryResponse> execute(String ownerLoginId, Long storeId){
        storeReader.requireOwnedStore(ownerLoginId, storeId);

        return inquiryRepository.findAllByStoreId(storeId)
                .stream()
                .map(InquiryResponse::new)
                .toList();
    }
}
