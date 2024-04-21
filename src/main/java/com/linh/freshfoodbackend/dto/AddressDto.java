package com.linh.freshfoodbackend.dto;

import com.linh.freshfoodbackend.utils.enums.AddressType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AddressDto {

    private String commune;
    private String district;
    private Integer cityId;
    private Integer countryId;
    private String fullAddress;
    private AddressType type;
}
