package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.dto.ProductDto;
import com.linh.freshfoodbackend.service.IProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/product")
@AllArgsConstructor
public class ProductController {

    private final IProductService productService;

    @GetMapping(path = "/getAll")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "16") Integer size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createTime") String sortBy,
            @RequestParam(name = "sortDir", required = false, defaultValue = "desc") String sortDir,
            @RequestParam(name = "categoryId", required = false) Integer categoryId
    ){
        return ResponseEntity.ok(productService.getAll(page, size, search, sortBy, sortDir, categoryId));
    }
    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody ProductDto request){
       return ResponseEntity.ok(productService.create(request));
    }

    @GetMapping(path = "/findByCategory")
    public ResponseEntity<?> findByCategory(@RequestParam(name = "categoryId") Integer categoryId){
        return ResponseEntity.ok(productService.findByCategory(categoryId));
    }

    @GetMapping(path = "/findById")
    public ResponseEntity<?> findById(@RequestParam(name = "id") Integer id){
        return ResponseEntity.ok(productService.findById(id));
    }

    @PutMapping(path = "/update")
    public ResponseEntity<?> update(@RequestBody ProductDto req){
        return ResponseEntity.ok(productService.update(req));
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<?> delete(@RequestParam Integer id){
        return ResponseEntity.ok(productService.delete(id));
    }
}
