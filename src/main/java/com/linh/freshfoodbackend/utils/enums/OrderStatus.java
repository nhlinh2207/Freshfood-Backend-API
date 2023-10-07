package com.linh.freshfoodbackend.utils.enums;

public enum OrderStatus {
    UNSENT("UNSENT", "Chưa giao"),
    PENDING("PENDING",  "Đang giao"),
    RATING("RATING",  "Chờ đánh giá"),
    COMPLETE("COMPLETE", "Đã giao"),
    DELETED("DELETED", "Đã hủy");

    public String code;
    public String message;

    OrderStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return message;
    }

    public static OrderStatus getByCode(String code) {
        for (OrderStatus e : OrderStatus.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
