package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.dto.request.category.CreateCategoryReq;
import com.linh.freshfoodbackend.service.ICategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/category")
@AllArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody CreateCategoryReq req){
        return ResponseEntity.ok(categoryService.create(req));
    }

    @GetMapping(path = "/getAll")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PutMapping(path = "/update")
    public ResponseEntity<?> update(@RequestBody CreateCategoryReq req){
        return ResponseEntity.ok(categoryService.update(req));
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") Integer id){
        return ResponseEntity.ok(categoryService.delete(id));
    }
}
