package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.CartDto;
import com.linh.freshfoodbackend.dto.request.cart.OrderReq;
import com.linh.freshfoodbackend.dto.response.PaginationResponse;
import com.linh.freshfoodbackend.dto.response.ResponseObject;

import java.util.Date;
import java.util.List;

public interface ICartService {

    ResponseObject<String> createOrder(OrderReq req);
    ResponseObject<PaginationResponse<Object>> findByUser(Integer page, String fromOrderTime, String toOrderTime);
}
