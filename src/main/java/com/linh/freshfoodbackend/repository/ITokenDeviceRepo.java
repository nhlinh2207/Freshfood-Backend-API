package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.TokenDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITokenDeviceRepo extends JpaRepository<TokenDevice, Integer> {
}
