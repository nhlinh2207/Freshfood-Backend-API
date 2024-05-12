package com.linh.freshfoodbackend.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SearchRequest {

    private String filter;

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;

    @NotNull
    private String sort;
}
