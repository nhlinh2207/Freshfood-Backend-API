package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}
