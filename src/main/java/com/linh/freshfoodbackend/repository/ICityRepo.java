package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICityRepo extends JpaRepository<City, Integer> {
}
