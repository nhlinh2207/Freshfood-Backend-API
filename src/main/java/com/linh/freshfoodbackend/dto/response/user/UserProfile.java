package com.linh.freshfoodbackend.dto.response.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserProfile {

    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Integer countryId;
    private Integer cityId;
    private String status;
    private String fullAddress;
}
