package com.linh.freshfoodbackend.dto.request.cart;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderReq {

    private String fullName;
    private String email;
    private String phone;
    private String fullAddress;
    private Integer countryId;
    private Integer cityId;
    private String orderMessage;
    private List<CartItemReq> cartItems;
}
