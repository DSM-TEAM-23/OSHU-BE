package com.dsm.oshu.timesale.service;

import com.dsm.oshu.timesale.domain.TimeSale;
import com.dsm.oshu.timesale.domain.TimeSaleRepository;
import com.dsm.oshu.timesale.exception.TimeSaleNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class TimeSaleReader {
    private final TimeSaleRepository timeSales;

    public TimeSaleReader(TimeSaleRepository timeSales) {
        this.timeSales = timeSales;
    }

    public TimeSale requireTimeSale(Long timeSaleId) {
        return timeSales.findById(timeSaleId)
                .orElseThrow(() -> new TimeSaleNotFoundException());
    }
}
