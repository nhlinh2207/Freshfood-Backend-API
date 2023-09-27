package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

public interface IRatingRepo extends JpaRepository<Rank, Integer> {

    @Procedure("GET_AVERAGE_RANK_VALUE_OF_PRODUCT")
    Float getAverageValueOfProduct(Integer productId);
}
