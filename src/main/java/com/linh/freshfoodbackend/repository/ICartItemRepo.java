package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICartItemRepo extends JpaRepository<CartItem, Integer> {
}
