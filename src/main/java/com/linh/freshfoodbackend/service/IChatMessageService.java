package com.linh.freshfoodbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linh.freshfoodbackend.entity.ChatMessage;

import java.util.List;

public interface IChatMessageService {

    void appendInstantMessageToConversations(ChatMessage message) throws JsonProcessingException;
    List<ChatMessage> findByChatRoomId(Integer chatRoomId);
}
