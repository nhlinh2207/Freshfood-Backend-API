package com.linh.freshfoodbackend.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Integer id;
    private String description;
    private String name;
    private String status;
}
