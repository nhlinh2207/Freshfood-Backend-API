package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

public interface IUserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    @Procedure("GET_ALL_USER")
    Integer getTotalUser();
}
