package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.dto.request.cart.OrderReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.service.ICartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/cart")
@AllArgsConstructor
public class CartController {

    private final ICartService cartService;

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody OrderReq req){
        return ResponseEntity.ok(cartService.createOrder(req));
    }


}
