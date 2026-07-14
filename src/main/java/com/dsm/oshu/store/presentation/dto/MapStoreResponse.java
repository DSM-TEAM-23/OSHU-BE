package com.dsm.oshu.store.presentation.dto;

import java.time.LocalDateTime;

public record MapStoreResponse(Long storeId, String name, String category, String address,
                               Double latitude, Double longitude, String crowdLevel,
                               String openingHours, boolean timeSaleActive, boolean externalData,
                               LocalDateTime timeSaleStartAt, LocalDateTime timeSaleEndAt,
                               Integer discountRate) {
}
