package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.CategoryDto;
import com.linh.freshfoodbackend.dto.request.category.CreateCategoryReq;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.entity.Category;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.ICategoryRepo;
import com.linh.freshfoodbackend.service.ICategoryService;
import com.linh.freshfoodbackend.utils.enums.CategoryStaus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService implements ICategoryService {

    private final ICategoryRepo categoryRepo;

    @Override
    public ResponseObject<String> create(CreateCategoryReq req) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Category category = Category.builder()
                    .name(req.getName())
                    .description(req.getDescription())
                    .status(CategoryStaus.ACTIVE)
                    .build();
            categoryRepo.saveAndFlush(category);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<List<CategoryDto>> getAll() {
        try{
            ResponseObject<List<CategoryDto>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            List<CategoryDto> data = categoryRepo.findAll().stream()
                            .map(
                                    i -> CategoryDto.builder().id(i.getId())
                                            .name(i.getName())
                                            .description(i.getDescription())
                                            .status(i.getStatus().getCode())
                                            .build()
                            ).collect(Collectors.toList());
            response.setData(data);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> update(CreateCategoryReq req) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Category category = categoryRepo.findById(req.getId()).orElseThrow(
                    () -> new UnSuccessException("Can not find category by id: "+req.getDescription())
            );
            category.setName(req.getName());
            category.setDescription(req.getDescription());
            categoryRepo.saveAndFlush(category);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> delete(Integer id) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Category category = categoryRepo.findById(id).orElseThrow(
                    () -> new UnSuccessException("Can not find category by id: "+id)
            );
//            categoryRepo.deleteById(id);
            category.setStatus(CategoryStaus.INACTIVE);
            categoryRepo.saveAndFlush(category);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> restore(Integer id) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Category category = categoryRepo.findById(id).orElseThrow(
                    () -> new UnSuccessException("Can not find category by id: "+id)
            );
//            categoryRepo.deleteById(id);
            category.setStatus(CategoryStaus.ACTIVE);
            categoryRepo.saveAndFlush(category);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }
}
