package com.linh.freshfoodbackend.dto.response.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserProfile {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Integer countryId;
    private Integer cityId;
    private String fullAddress;
}
