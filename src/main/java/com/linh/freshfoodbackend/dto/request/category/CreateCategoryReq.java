package com.linh.freshfoodbackend.dto.request.category;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryReq {

    private Integer id;
    private String name;
    private String description;
}
