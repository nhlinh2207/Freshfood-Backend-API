package com.linh.freshfoodbackend.dto.request;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserReq {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private Integer countryId;
    private Integer cityId;
    private String fullAddress;

}
