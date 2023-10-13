package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.CartDto;
import com.linh.freshfoodbackend.dto.request.cart.OrderReq;
import com.linh.freshfoodbackend.dto.response.PaginationResponse;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import org.json.JSONObject;

public interface ICartService {

    ResponseObject<String> createOrder(OrderReq req);
    ResponseObject<PaginationResponse<Object>> findByUser(Integer page, String fromOrderTime, String toOrderTime);
    ResponseObject<String> delete(Integer cartId);
    ResponseObject<CartDto> findById(Integer cartId);
    ResponseObject<PaginationResponse<Object>> findAll(Integer page, String fromOrderTime, String toOrderTime, String type);
    ResponseObject<String>  createZaloPay(OrderReq req);
    ResponseObject<String> assignToStaff(Integer cartId, Integer staffId);
    ResponseObject<String> deliveryCart(Integer cartId);
    ResponseObject<String> receiveCart(Integer cartId);
}
