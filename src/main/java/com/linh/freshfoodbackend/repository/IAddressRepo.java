package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAddressRepo extends JpaRepository<Address, Integer> {
}
