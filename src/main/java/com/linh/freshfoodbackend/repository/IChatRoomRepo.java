package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IChatRoomRepo extends JpaRepository<ChatRoom, Integer> {

    ChatRoom findByUserId(Integer userId);
    List<ChatRoom> findByAdminId(Integer adminId);
}
