package com.dsm.oshu.store.presentation.dto;

import com.dsm.oshu.promotion.presentation.dto.PromotionResponse;
import com.dsm.oshu.timesale.presentation.dto.TimeSaleResponse;
import java.util.List;

public record StoreDetailResponse(Long storeId, String name, String category, String description, String address,
                                  Double latitude, Double longitude, String phone, String openingHours,
                                  CrowdStatusResponse crowdStatus, List<PromotionResponse> promotions,
                                  List<TimeSaleResponse> timeSales) {
}
