package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IAddressRepo extends JpaSpecificationExecutor<Address>, JpaRepository<Address, Integer> {
}
