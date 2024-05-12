package com.linh.freshfoodbackend.dto.mapper;

import com.linh.freshfoodbackend.dto.ProductDto;
import com.linh.freshfoodbackend.entity.Product;

import java.time.ZonedDateTime;
import java.util.Date;

public class ProductMapper {

    public static ProductDto mapEntityToDto(Product product){
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .mainImage(product.getImage())
                .extraImage1(product.getExtra_img1())
                .extraImage2(product.getExtra_img2())
                .categoryId(product.getCategory().getId())
                .createTime(product.getCreateTime())
                .updateTime(product.getUpdateTime())
                .build();
    }

    public static Product mapDtoToEntity(ProductDto productDto){
        return Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .quantity(productDto.getQuantity())
                .image(productDto.getMainImage())
                .extra_img1(productDto.getExtraImage1())
                .extra_img2(productDto.getExtraImage2())
                .buyingCount(0)
                .createTime(ZonedDateTime.now())
                .updateTime(ZonedDateTime.now())
                .build();
    }
}
