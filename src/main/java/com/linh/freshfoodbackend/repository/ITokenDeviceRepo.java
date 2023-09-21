package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.TokenDevice;
import com.linh.freshfoodbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITokenDeviceRepo extends JpaRepository<TokenDevice, Integer> {

    TokenDevice findByUser(User user);
}
