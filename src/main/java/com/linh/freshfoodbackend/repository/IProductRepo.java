package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Category;
import com.linh.freshfoodbackend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IProductRepo extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT p FROM Product p WHERE (:search IS NULL OR p.name LIKE %:search% OR CAST(p.price AS string) LIKE %:search% ) AND (:category IS NULL OR p.category = :category)")
    Page<Product> getAll(@Param("search") String search, @Param("category")Category category, Pageable pageable);
}
