package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.City;
import com.linh.freshfoodbackend.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICityRepo extends JpaRepository<City, Integer> {

    List<City> findByCountry(Country country);
}
