package com.linh.freshfoodbackend.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class ResponseObject<T> {
    private Boolean result;
    private Integer code;
    private String message;
    private T data;

   // Use for Redis Cache
    public ResponseObject(){
        super();
    }

    public ResponseObject(Boolean result, ResponseStatus status) {
        this.result = result;
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    public ResponseObject(Boolean result, ResponseStatus status, String message) {
        this.result = result;
        this.code = status.getCode();
        this.message = message;
    }

    public ResponseObject(Boolean result, ResponseStatus status, T data) {
        this.result = result;
        this.code = status.getCode();
        this.message = status.getMessage();
        this.data = data;
    }
}
