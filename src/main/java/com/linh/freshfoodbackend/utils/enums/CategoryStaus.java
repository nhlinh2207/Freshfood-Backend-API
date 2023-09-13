package com.linh.freshfoodbackend.utils.enums;

public enum CategoryStaus {

    ACTIVE("ACTIVE", "Hoạt động"),
    INACTIVE("INACTIVE", "Không hoạt động");

    public String code;
    public String message;

    CategoryStaus(String code, String message) {
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
