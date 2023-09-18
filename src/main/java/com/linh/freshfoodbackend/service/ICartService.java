package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.request.cart.OrderReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;

public interface ICartService {

    ResponseObject<String> createOrder(OrderReq req);
}
