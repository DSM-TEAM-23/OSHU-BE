package com.dsm.oshu.timesale.service;

import com.dsm.oshu.timesale.presentation.dto.TimeSaleRequest;
import com.dsm.oshu.timesale.presentation.dto.TimeSaleResponse;
import com.dsm.oshu.common.service.PeriodValidator;
import com.dsm.oshu.store.service.StoreReader;
import com.dsm.oshu.timesale.service.TimeSaleDtoMapper;
import com.dsm.oshu.timesale.service.TimeSaleReader;
import com.dsm.oshu.store.domain.Store;
import com.dsm.oshu.timesale.domain.TimeSale;
import com.dsm.oshu.timesale.domain.TimeSaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OwnerTimeSaleService {
    private final TimeSaleRepository timeSales;
    private final StoreReader storeReader;
    private final TimeSaleReader timeSaleReader;
    private final TimeSaleDtoMapper timeSaleDtoMapper;
    private final PeriodValidator periodValidator;

    public OwnerTimeSaleService(TimeSaleRepository timeSales, StoreReader storeReader,
                                TimeSaleReader timeSaleReader, TimeSaleDtoMapper timeSaleDtoMapper,
                                PeriodValidator periodValidator) {
        this.timeSales = timeSales;
        this.storeReader = storeReader;
        this.timeSaleReader = timeSaleReader;
        this.timeSaleDtoMapper = timeSaleDtoMapper;
        this.periodValidator = periodValidator;
    }

    @Transactional
    public TimeSaleResponse createTimeSale(String ownerLoginId, Long storeId,
                                                    TimeSaleRequest request) {
        periodValidator.validateTimeSale(request);
        Store store = storeReader.requireOwnedStore(ownerLoginId, storeId);
        TimeSale timeSale = timeSales.save(new TimeSale(
                store,
                request.productName(),
                request.originalPrice(),
                request.salePrice(),
                request.startAt(),
                request.endAt(),
                request.notice()));
        return timeSaleDtoMapper.toTimeSale(timeSale);
    }

    @Transactional
    public TimeSaleResponse updateTimeSale(String ownerLoginId, Long timeSaleId,
                                                    TimeSaleRequest request) {
        periodValidator.validateTimeSale(request);
        TimeSale timeSale = timeSaleReader.requireTimeSale(timeSaleId);
        storeReader.assertOwner(ownerLoginId, timeSale.getStore());
        timeSale.update(request.productName(), request.originalPrice(), request.salePrice(),
                request.startAt(), request.endAt(), request.notice());
        return timeSaleDtoMapper.toTimeSale(timeSale);
    }

    @Transactional
    public TimeSaleResponse closeTimeSale(String ownerLoginId, Long timeSaleId) {
        TimeSale timeSale = timeSaleReader.requireTimeSale(timeSaleId);
        storeReader.assertOwner(ownerLoginId, timeSale.getStore());
        timeSale.close();
        return timeSaleDtoMapper.toTimeSale(timeSale);
    }
}
