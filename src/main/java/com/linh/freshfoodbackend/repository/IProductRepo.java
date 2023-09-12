package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductRepo extends JpaRepository<Product, Integer> {
}
