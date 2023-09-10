package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICountryRepo extends JpaRepository<Country, Integer> {
}
