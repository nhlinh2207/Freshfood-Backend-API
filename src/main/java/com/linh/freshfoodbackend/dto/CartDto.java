package com.linh.freshfoodbackend.dto;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Integer id;
    private Integer productQuantity;
    private Integer totalPrice;
    private String receiverName;
    private String phoneNumber;
    private String email;
    private String status;
    private String address;
    private String orderTime;
    private String deliveryTime;
    private Integer staffId;
    private List<CartItemDto> cartItems;
    private Boolean isDelivered;
    private Boolean isReceived;
    private Boolean isPaid;
}
