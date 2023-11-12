package com.linh.freshfoodbackend.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JasperDto {

//    Cart
    private Integer productId;
    private String productName;
    private String image;
    private Integer quantity;
    private String price;
    private String itemTotalPrice;

}
