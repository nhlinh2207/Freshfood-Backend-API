package com.linh.freshfoodbackend.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private Integer quantity;
    private String mainImage;
    private String extraImage1;
    private String extraImage2;
    private Integer categoryId;

}
