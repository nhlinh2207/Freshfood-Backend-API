package com.linh.freshfoodbackend.utils.enums;

public enum AddressType {

    RESIDENT("RESIDENT", "Địa chỉ nhà"),
    DELIVERY("DELIVERY", "Địa chỉ giao hàng");

    public String code;
    public String message;

    AddressType(String code, String message) {
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
