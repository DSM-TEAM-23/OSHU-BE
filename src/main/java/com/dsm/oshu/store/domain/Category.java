package com.dsm.oshu.store.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    BAKERY("베이커리"),
    RESTAURANT("음식점"),
    CAFE("카페"),
    MART("마트"),
    MARKET("시장"),
    GROCERY("식료품"),
    OTHER("기타");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Category from(String value) {
        for (Category category : values()) {
            if (category.name().equalsIgnoreCase(value) || category.label.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 가게 카테고리입니다.");
    }

    public boolean matches(String value) {
        return name().equalsIgnoreCase(value) || label.equals(value);
    }
}
