package com.dsm.oshu.store.presentation.dto;

public record StoreCardResponse(Long storeId, String name, String category, String address,
                                Double latitude, Double longitude, String crowdLevel,
                                boolean timeSaleActive, boolean externalData) {
}
