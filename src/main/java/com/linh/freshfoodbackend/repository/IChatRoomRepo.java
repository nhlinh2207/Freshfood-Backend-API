package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface IChatRoomRepo extends JpaRepository<ChatRoom, Integer> {

    ChatRoom findByUserId(Integer userId);
    @Query(value = "CALL GET_CHATROOM_BY_ADMIN(:adminId)", nativeQuery = true)
    List<Map<String, Object>> findByAdminId(@Param("adminId") Integer adminId);
}
