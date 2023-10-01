package com.linh.freshfoodbackend.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    private Integer productId;
    private String productName;
    private String extraImage1;
    private Integer quantity;
    private Integer productPrice;
    private Integer totalPrice;
}
