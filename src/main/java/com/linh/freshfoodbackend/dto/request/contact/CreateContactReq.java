package com.linh.freshfoodbackend.dto.request.contact;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateContactReq {

    private String fullName;
    private String email;
    private String content;
}
