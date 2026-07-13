package com.dsm.oshu.promotion.exception;

public class PromotionNotFoundException extends RuntimeException {
    public PromotionNotFoundException() {
        super("홍보 게시물을 찾을 수 없습니다.");
    }
}
