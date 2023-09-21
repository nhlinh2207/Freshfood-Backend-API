package com.linh.freshfoodbackend.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDeviceDto {

    private Integer id;
    private String webToken;
    private String iosToken;
    private String androidToken;
}
