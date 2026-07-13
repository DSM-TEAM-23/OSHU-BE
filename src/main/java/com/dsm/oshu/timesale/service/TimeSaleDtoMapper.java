package com.dsm.oshu.timesale.service;

import com.dsm.oshu.timesale.presentation.dto.TimeSaleResponse;
import com.dsm.oshu.timesale.domain.TimeSale;
import org.springframework.stereotype.Component;

@Component
public class TimeSaleDtoMapper {
    public TimeSaleResponse toTimeSale(TimeSale sale) {
        return new TimeSaleResponse(
                sale.getId(),
                sale.getStore().getId(),
                sale.getProductName(),
                sale.getOriginalPrice(),
                sale.getSalePrice(),
                sale.getStartAt(),
                sale.getEndAt(),
                sale.getNotice(),
                sale.getStatus());
    }
}
