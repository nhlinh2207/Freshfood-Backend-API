package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.dto.mapper.ProductMapper;
import com.linh.freshfoodbackend.dto.request.SearchRequest;
import com.linh.freshfoodbackend.dto.sqlSearchDto.CustomRsqlVisitor;
import com.linh.freshfoodbackend.entity.Product;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.IProductRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(path = "/test")
@Slf4j
@AllArgsConstructor
public class TestController {

    private final IProductRepo productRepo;

    @GetMapping(path = "/emch")
    @Cacheable(value = "emchCache", key = "#id", unless = "#result == null")
    public String loginemch(@RequestParam("id") Long id){
        try{
            log.info("Login emch  ->>> okokok");
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("tenDangNhap", "02929");
            requestBody.add("matKhau", "MTIzNEBBYmNk");

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");
            HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> responseEntity = restTemplate.postForEntity("https://skbmte-qa.orenda.vn/api/lien-thong/tai-khoan/dang-nhap", requestEntity, Map.class);
            Map<String, String> responseBody = responseEntity.getBody();
            return responseBody.get("accessToken");
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException("Login EMCH failed !");
        }
    }

//    Tìm kiếm : {{product}}/test/search?page=0&size=10&filter=createTime>="14/11/2023 00:00:00";createTime<="12/05/2024 23:59:59";name=="*wi*"&sort=id,desc
    @GetMapping(path = "/search")
    public ResponseEntity<?> searchProduct(@Valid SearchRequest req){
        Node rootNode = new RSQLParser().parse(req.getFilter());
        Specification<Product> spec = rootNode.accept(new CustomRsqlVisitor<>());
        log.info("search: {}", req.getFilter());

        // Create Page
        String[] sortList = req.getSort().split(",");
        Sort.Direction direction = sortList[1].equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = req.getSize() != null ? PageRequest.of(req.getPage(), req.getSize(), direction, sortList[0]) : Pageable.unpaged();

        Page<Product> productPage = productRepo.findAll(spec, pageable);
        return ResponseEntity.ok(productPage.map(ProductMapper::mapEntityToDto));
    }

}
