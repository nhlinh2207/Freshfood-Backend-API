package com.linh.freshfoodbackend.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Integer id;
    private Integer productQuantity;
    private Integer totalPrice;
    private String status;
    private String orderTime;
    private String deliveryTime;
}
