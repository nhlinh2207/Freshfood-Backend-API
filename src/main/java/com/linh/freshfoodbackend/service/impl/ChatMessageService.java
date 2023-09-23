package com.linh.freshfoodbackend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linh.freshfoodbackend.entity.ChatMessage;
import com.linh.freshfoodbackend.repository.IChatMessageRepo;
import com.linh.freshfoodbackend.service.IChatMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ChatMessageService implements IChatMessageService {

    private final IChatMessageRepo messageRepo;

    @Override
    public void appendInstantMessageToConversations(ChatMessage message) throws JsonProcessingException {
        if (message.isPublic()) {
            messageRepo.save(ChatMessage.builder()
                    .chatRoomId(message.getChatRoomId())
                    .content(message.getContent())
                    .fromUser(message.getFromUser())
                    .username(message.getFromUser())
                    .senderType(message.getSenderType())
                    .createTime(new Date())
                    .build());
        } else {
            messageRepo.save(ChatMessage.builder()
                    .chatRoomId(message.getChatRoomId())
                    .content(message.getContent())
                    .fromUser(message.getFromUser())
                    .toUser(message.getToUser())
                    .username(message.getFromUser())
                    .build()
            );

            messageRepo.save(ChatMessage.builder()
                    .chatRoomId(message.getChatRoomId())
                    .content(message.getContent())
                    .fromUser(message.getFromUser())
                    .toUser(message.getToUser())
                    .username(message.getToUser())
                    .build()
            );
        }
    }

    @Override
    public List<ChatMessage> findByChatRoomId(Integer chatRoomId) {
        return messageRepo.findByChatRoomId(chatRoomId);
    }
}
