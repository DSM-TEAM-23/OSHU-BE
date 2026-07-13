package com.dsm.oshu.timesale.presentation.dto;

import java.time.LocalDateTime;

public record TimeSaleResponse(Long timeSaleId, Long storeId, String productName, Integer originalPrice,
                               Integer salePrice, LocalDateTime startAt, LocalDateTime endAt, String notice,
                               String status) {
}
