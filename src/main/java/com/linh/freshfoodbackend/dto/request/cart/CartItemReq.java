package com.linh.freshfoodbackend.dto.request.cart;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemReq {
    private Integer id;
    private String name;
    private Integer qty;
    private Integer sum;
}
