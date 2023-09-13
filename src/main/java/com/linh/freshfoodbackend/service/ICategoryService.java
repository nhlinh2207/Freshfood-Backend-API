package com.linh.freshfoodbackend.service;

import com.linh.freshfoodbackend.dto.CategoryDto;
import com.linh.freshfoodbackend.dto.request.category.CreateCategoryReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;

import java.util.List;

public interface ICategoryService {

    ResponseObject<String> create(CreateCategoryReq req);
    ResponseObject<List<CategoryDto>> getAll();

    ResponseObject<String> update(CreateCategoryReq req);

    ResponseObject<String> delete(Integer id);
}
