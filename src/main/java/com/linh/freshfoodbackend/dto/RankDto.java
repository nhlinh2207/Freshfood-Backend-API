package com.linh.freshfoodbackend.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RankDto {

     private Integer rankNumber;
     private String rankContent;
     private Integer productId;
     private String rankCustomerName;
     private String senderEmail;
}
