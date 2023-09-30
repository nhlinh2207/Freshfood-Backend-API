package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Cart;
import com.linh.freshfoodbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ICartRepo extends JpaRepository<Cart, Integer> {

    @Procedure("GET_MONTHLY_INCOME")
    Integer getMonthIncome(Integer year, Integer month);

    @Procedure("GET_ANNUAL_INCOME")
    Integer getAnnualIncome(Integer year);

    @Query(value = "CALL LIST_MONTHLY_INCOME_BY_YEAR(:year)", nativeQuery = true)
    List<Map<String, Object>> listMonthlyIncomeByYear(@Param("year") Integer year);

    @Query("SELECT c FROM Cart c WHERE c.user = :user AND c.orderTime >= :fromOrderTime AND c.orderTime <= :toOrderTime")
    Page<Cart> findByUser(@Param("user") User user,
                          @Param("fromOrderTime") Date fromOrderTime,
                          @Param("toOrderTime")Date toOrderTime,
                          Pageable pageable);
}
