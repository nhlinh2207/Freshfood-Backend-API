package com.linh.freshfoodbackend.repository;

import com.linh.freshfoodbackend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface IChatMessageRepo extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByChatRoomId(Integer chatRoomId);
    @Procedure("GET_LATEST_MESSAGE")
    String getLatestMessage(Integer chatRoomId);
}
