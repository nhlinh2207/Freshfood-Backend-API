package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.dto.request.cart.OrderReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.service.ICartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(path = "/cart")
@AllArgsConstructor
public class CartController {

    private final ICartService cartService;

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody OrderReq req){
        return ResponseEntity.ok(cartService.createOrder(req));
    }


    @GetMapping(path = "/findByUser")
    public ResponseEntity<?> findByUser(@RequestParam(name = "page") Integer page,
                                        @RequestParam(name = "fromOrderTime", required = false, defaultValue = "") String fromOrderTime,
                                        @RequestParam(name = "toOrderTime", required = false, defaultValue = "") String toOrderTime){
        return ResponseEntity.ok(cartService.findByUser(page, fromOrderTime, toOrderTime));
    }

}
