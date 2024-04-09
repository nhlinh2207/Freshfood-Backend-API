package com.linh.freshfoodbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linh.freshfoodbackend.dto.ChatRoomDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.entity.ChatMessage;
import com.linh.freshfoodbackend.entity.ChatRoom;
import com.linh.freshfoodbackend.entity.ConnectedUser;

import java.util.List;

public interface IChatRoomService {

    ChatRoom save(ChatRoom chatRoom);
    List<ChatRoom> findAll();
    ResponseObject<ChatRoomDto> findByUser();
    ResponseObject<List<ChatRoomDto>> findByAdmin();
    ChatRoom findById(Integer id);
    ChatRoom join(Integer chatRoomId, ConnectedUser user);
    ChatRoom leave(Integer chatRoomId, ConnectedUser user) throws JsonProcessingException;
    void sendPublicMessage(ChatMessage message) throws JsonProcessingException;
    void sendPrivateMessage(ChatMessage message) throws JsonProcessingException;
    void loadOldMessage(Integer chatRoomId, String username);

    void reloadAllChatRoom(String chatRoomId);
}
