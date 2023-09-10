package com.linh.freshfoodbackend.exception;

import com.linh.freshfoodbackend.dto.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(UnSuccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseObject<Object> UnSuccessHandler(UnSuccessException ex) {
        return new ResponseObject<>(false, com.linh.freshfoodbackend.dto.response.ResponseStatus.UNSUCCESS, ex.getMessage());
    }
}
