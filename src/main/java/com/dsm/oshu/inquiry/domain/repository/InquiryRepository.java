package com.dsm.oshu.inquiry.domain.repository;

import com.dsm.oshu.inquiry.domain.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findAllByStoreId(Long storeId);
}
