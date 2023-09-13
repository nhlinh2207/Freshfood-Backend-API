package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.ProductDto;
import com.linh.freshfoodbackend.dto.response.PaginationResponse;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.entity.Category;

import java.util.List;

public interface IProductService {

    ResponseObject<PaginationResponse<Object>> getAll(Integer page, Integer size, String search,
                                                      String sortBy, String sortDir, Integer categoryId);
    ResponseObject<String> create(ProductDto request);

    ResponseObject<ProductDto> findById(Integer id);

    ResponseObject<String> update(ProductDto req);

    ResponseObject<String> delete(Integer id);

}
