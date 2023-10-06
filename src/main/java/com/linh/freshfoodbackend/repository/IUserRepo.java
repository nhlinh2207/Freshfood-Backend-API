package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.User;
import com.linh.freshfoodbackend.utils.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface IUserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    @Procedure("GET_ALL_USER")
    Integer getTotalUser();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE (r.name = :type) AND " +
            " (:status IS NULL OR u.status = :status) AND" +
            " (:search IS NULL OR (CONCAT(u.firstName, ' ', u.lastName) LIKE %:search%))")
    Page<User> getAll(@Param("search") String search,
                      @Param("type") String type,
                      @Param("status") UserStatus status,
                      Pageable pageable);
}
