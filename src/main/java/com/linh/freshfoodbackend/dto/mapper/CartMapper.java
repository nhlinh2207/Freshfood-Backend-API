package com.linh.freshfoodbackend.dto.mapper;

import com.linh.freshfoodbackend.dto.CartDto;
import com.linh.freshfoodbackend.entity.Cart;
import com.linh.freshfoodbackend.entity.CartItem;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartDto mapToCartDto(Cart c){
        SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return CartDto.builder()
                .id(c.getId())
                .productQuantity( c.getCartItems().stream().mapToInt(CartItem::getQuantity).sum())
                .totalPrice(c.getTotalPrice())
                .receiverName(c.getReceiverName())
                .email(c.getReceiverEmail())
                .phoneNumber(c.getReceiverPhoneNumber())
                .address(c.getAddress().getFullAddress())
                .status(c.getStatus().message)
                .staffId(c.getStaff() == null ? 0 : c.getStaff().getId())
                .orderTime(smf.format(c.getOrderTime()))
                .deliveryTime(c.getDeliveryTime() == null ? "" : smf.format(c.getDeliveryTime()))
                .isDelivered(c.getIsDelivered())
                .isPaid(c.getIsPaid())
                .isReceived(c.getIsReceived())
                .cartItems(c.getCartItems().stream().map(CartItemMapper::mapCartItemToDto).collect(Collectors.toList()))
                .build();
    }
}
