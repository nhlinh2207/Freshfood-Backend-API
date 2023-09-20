package com.linh.freshfoodbackend.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CountryDto {

    private Integer id;
    private String name;
}
