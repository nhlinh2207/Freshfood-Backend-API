package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.dto.request.cart.OrderReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.service.ICartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping(path = "/zaloPay/create")
    public ResponseEntity<?> createZaloPay(@RequestBody OrderReq req){
        return ResponseEntity.ok(cartService.createZaloPay(req));
    }

    @GetMapping(path = "/findByUser")
    public ResponseEntity<?> findByUser(@RequestParam(name = "page") Integer page,
                                        @RequestParam(name = "fromOrderTime", required = false, defaultValue = "") String fromOrderTime,
                                        @RequestParam(name = "toOrderTime", required = false, defaultValue = "") String toOrderTime){
        return ResponseEntity.ok(cartService.findByUser(page, fromOrderTime, toOrderTime));
    }

    @GetMapping(path = "/findAll")
    public ResponseEntity<?> findAll(@RequestParam(name = "page") Integer page,
                                     @RequestParam(name = "fromOrderTime", required = false, defaultValue = "") String fromOrderTime,
                                     @RequestParam(name = "toOrderTime", required = false, defaultValue = "") String toOrderTime,
                                     @RequestParam(name = "status", required = false) String status){
        return ResponseEntity.ok(cartService.findAll(page, fromOrderTime, toOrderTime, status));
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "cartId") Integer cartId){
        return ResponseEntity.ok(cartService.delete(cartId));
    }

    @GetMapping(path = "/findById")
    public ResponseEntity<?> findById(@RequestParam(name = "cartId") Integer cartId){
        return ResponseEntity.ok(cartService.findById(cartId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/assignToStaff")
    public ResponseEntity<?> assignToStaff(@RequestParam(name = "cartId") Integer cartId, @RequestParam(name = "staffId") Integer staffId){
        return ResponseEntity.ok(cartService.assignToStaff(cartId, staffId));
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping(path = "/delivery")
    public ResponseEntity<?> deliveryCart(@RequestParam(name = "cartId") Integer cartId){
        return ResponseEntity.ok(cartService.delete(cartId));
    }

    @GetMapping(path = "/receive")
    public ResponseEntity<?> receiveCart(@RequestParam(name = "cartId") Integer cartId){
        return ResponseEntity.ok(cartService.receiveCart(cartId));
    }
}
