package com.linh.freshfoodbackend.dto.mapper;

import com.linh.freshfoodbackend.dto.CartItemDto;
import com.linh.freshfoodbackend.entity.CartItem;

public class CartItemMapper {

    public static CartItemDto mapCartItemToDto(CartItem item){
        return CartItemDto.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .productPrice(item.getProduct().getPrice())
                .extraImage1(item.getProduct().getExtra_img1())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
