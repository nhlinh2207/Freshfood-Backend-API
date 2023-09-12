package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoryRepo extends JpaRepository<Category, Integer> {
}
