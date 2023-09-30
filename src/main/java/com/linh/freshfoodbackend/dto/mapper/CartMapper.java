package com.linh.freshfoodbackend.dto.mapper;

import com.linh.freshfoodbackend.dto.CartDto;
import com.linh.freshfoodbackend.entity.Cart;
import com.linh.freshfoodbackend.entity.CartItem;

import java.text.SimpleDateFormat;

public class CartMapper {

    public static CartDto mapToCartDto(Cart c){
        SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return CartDto.builder()
                .id(c.getId())
                .productQuantity( c.getCartItems().stream().mapToInt(CartItem::getQuantity).sum())
                .totalPrice(c.getTotalPrice())
                .status(c.getStatus().message)
                .orderTime(smf.format(c.getOrderTime()))
                .deliveryTime(c.getDeliveryTime() == null ? "" : smf.format(c.getDeliveryTime()))
                .build();
    }
}
