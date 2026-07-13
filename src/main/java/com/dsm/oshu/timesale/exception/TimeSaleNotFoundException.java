package com.dsm.oshu.timesale.exception;

public class TimeSaleNotFoundException extends RuntimeException {
    public TimeSaleNotFoundException() {
        super("타임세일을 찾을 수 없습니다.");
    }
}
