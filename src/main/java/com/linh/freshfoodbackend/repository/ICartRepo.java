package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.Cart;
import com.linh.freshfoodbackend.entity.User;
import com.linh.freshfoodbackend.utils.enums.OrderStatus;
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

    @Query("SELECT c FROM Cart c WHERE c.staff = :user AND c.orderTime >= :fromOrderTime AND c.orderTime <= :toOrderTime")
    Page<Cart> findByStaff(@Param("user") User user,
                          @Param("fromOrderTime") Date fromOrderTime,
                          @Param("toOrderTime")Date toOrderTime,
                          Pageable pageable);

    @Query("SELECT c FROM Cart c WHERE c.orderTime >= :fromOrderTime " +
            "AND c.orderTime <= :toOrderTime " +
            "AND (:status IS NULL OR c.status = :status)")
    Page<Cart> findAll(@Param("fromOrderTime") Date fromOrderTime,
                       @Param("toOrderTime")Date toOrderTime,
                       @Param("status") OrderStatus status,
                       Pageable pageable);

}
