package com.linh.freshfoodbackend.utils.enums;

public enum UserStatus {

    ACTIVE("ACTIVE", "Hoạt động"),
    INACTIVE("INACTIVE", "Không hoạt động"),
    DELETED("DELETED", "Đã xóa");
    public String code;
    public String message;

    UserStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return message;
    }

    public static UserStatus getByCode(String code) {
        for (UserStatus e : UserStatus.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
