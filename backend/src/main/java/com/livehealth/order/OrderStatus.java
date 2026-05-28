package com.livehealth.order;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderStatus {
    ORDER_RECEIVED,
    PROCESSING,
    ON_THE_WAY,
    DELIVERED,
    COMPLETED,
    CANCELLED;

    @JsonCreator
    public static OrderStatus fromString(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        if ("PENDING".equalsIgnoreCase(trimmed)) {
            return ORDER_RECEIVED;
        }
        try {
            return OrderStatus.valueOf(trimmed.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
