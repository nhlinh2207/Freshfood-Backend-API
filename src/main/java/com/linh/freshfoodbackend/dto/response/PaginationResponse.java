package com.linh.freshfoodbackend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PaginationResponse <T>{
    private Long totalItems;
    private Integer size;
    private T data;
    private Integer totalPages;
    private Integer currentPage;
}
