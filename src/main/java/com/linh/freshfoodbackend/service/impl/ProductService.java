package com.linh.freshfoodbackend.service.impl;

import com.linh.freshfoodbackend.dto.ProductDto;
import com.linh.freshfoodbackend.dto.mapper.ProductMapper;
import com.linh.freshfoodbackend.dto.response.PaginationResponse;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.entity.Category;
import com.linh.freshfoodbackend.entity.Product;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.ICategoryRepo;
import com.linh.freshfoodbackend.repository.IProductRepo;
import com.linh.freshfoodbackend.service.IProductService;
import com.linh.freshfoodbackend.utils.PaginationCustom;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService implements IProductService {

    private final IProductRepo productRepo;
    private final ICategoryRepo categoryRepo;
    @Override
    public ResponseObject<PaginationResponse<Object>> getAll(Integer page, Integer size, String search,
                                                             String sortBy, String sortDir, Integer categoryId) {
        try{
            Category category = categoryId == null ? null : categoryRepo.findById(categoryId).orElseThrow(
                    () -> new UnSuccessException("Can not find category with id : "+categoryId)
            );
            ResponseObject<PaginationResponse<Object>> res = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Pageable pageable = PaginationCustom.createPaginationCustom(page, size, sortBy, sortDir);
            Page<Product> productPage = productRepo.getAll(search, category, pageable);
            List<ProductDto> productData = productPage.getContent().stream().map(
                    ProductMapper::mapEntityToDto
            ).collect(Collectors.toList());
            // Create response
            res.setData(PaginationResponse.builder()
                    .currentPage(productPage.getNumber())
                    .size(productPage.getSize())
                    .totalItems(productPage.getTotalElements())
                    .totalPages(productPage.getTotalPages())
                    .data(productData)
                    .build());
            return res;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> create(ProductDto request) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Category category = categoryRepo.findById(request.getCategoryId()).orElseThrow(
                    () -> new UnSuccessException("Can not find category with id : "+request.getCategoryId())
            );

            Product product = ProductMapper.mapDtoToEntity(request);
            product.setCategory(category);
            productRepo.saveAndFlush(product);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<ProductDto> findById(Integer id) {
        try{
            ResponseObject<ProductDto> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Product product = productRepo.findById(id).orElseThrow(
                    () -> new UnSuccessException("Can not find product with id: "+id)
            );
            response.setData(ProductMapper.mapEntityToDto(product));
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<String> update(ProductDto req) {
        try{
            ResponseObject<String> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Category category = categoryRepo.findById(req.getCategoryId()).orElseThrow(
                    () -> new UnSuccessException("Can not find category with id : "+req.getCategoryId())
            );
            Product product = productRepo.findById(req.getId()).orElseThrow(
                    () -> new UnSuccessException("Can not find product with id: "+req.getId())
            );
            product.setName(req.getName());
            product.setDescription(req.getDescription());
            product.setPrice(req.getPrice());
            product.setQuantity(req.getQuantity());
            product.setImage(req.getMainImage());
            product.setExtra_img1(req.getExtraImage1());
            product.setExtra_img2(req.getExtraImage2());
            product.setCategory(category);
            productRepo.saveAndFlush(product);
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
            productRepo.deleteById(id);
            response.setData("Success");
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<List<ProductDto>> findByCategory(Integer categoryId) {
        try{
            ResponseObject<List<ProductDto>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            Category category = categoryRepo.findById(categoryId).orElseThrow(
                    () -> new UnSuccessException("Can not find category with id: "+categoryId)
            );
            List<ProductDto> data = productRepo.findByCategory(category).stream().map(
                    ProductMapper::mapEntityToDto
            ).collect(Collectors.toList());;
            response.setData(data);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }


}
