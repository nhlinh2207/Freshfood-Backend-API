package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleRepo extends JpaRepository<Role, Integer> {

    Role findByName(String name);
}
