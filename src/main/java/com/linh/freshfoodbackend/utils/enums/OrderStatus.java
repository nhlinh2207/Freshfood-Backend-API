package com.linh.freshfoodbackend.utils.enums;

public enum OrderStatus {
    UNSENT("UNSENT", "Chưa giao"),
    PENDING("PENDING",  "Đang giao"),

    RATING("RATING",  "Chờ đánh giá"),
    COMPLETE("COMPLETE", "Đã giao");

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
}
