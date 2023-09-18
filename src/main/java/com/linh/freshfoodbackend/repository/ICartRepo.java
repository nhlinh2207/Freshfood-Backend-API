package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICartRepo extends JpaRepository<Cart, Integer> {
}
