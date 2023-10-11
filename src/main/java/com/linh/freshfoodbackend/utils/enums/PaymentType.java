package com.linh.freshfoodbackend.utils.enums;

public enum PaymentType {

    OFFLINE("OFFLINE", "Thanh "),
    ZALOPAY("ZALOPAY", "vVí ZaloPay");

    public String code;
    public String message;

    PaymentType(String code, String message) {
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
