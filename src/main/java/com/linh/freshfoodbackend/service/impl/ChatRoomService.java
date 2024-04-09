package com.linh.freshfoodbackend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linh.freshfoodbackend.dto.ChatRoomDto;
import com.linh.freshfoodbackend.dto.response.ResponseObject;
import com.linh.freshfoodbackend.dto.response.ResponseStatus;
import com.linh.freshfoodbackend.entity.ChatMessage;
import com.linh.freshfoodbackend.entity.ChatRoom;
import com.linh.freshfoodbackend.entity.ConnectedUser;
import com.linh.freshfoodbackend.entity.User;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.IChatRoomRepo;
import com.linh.freshfoodbackend.service.IChatMessageService;
import com.linh.freshfoodbackend.service.IChatRoomService;
import com.linh.freshfoodbackend.service.IUserService;
import com.linh.freshfoodbackend.utils.Destination;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ChatRoomService implements IChatRoomService {

    private final IChatRoomRepo chatRoomRepo;
    private final SimpMessagingTemplate webSocketMessagingTemplate;
    private final IChatMessageService messageService;
    private final IUserService userService;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return null;
    }

    @Override
    public List<ChatRoom> findAll() {
        return chatRoomRepo.findAll();
    }

    @Override
    public ResponseObject<ChatRoomDto> findByUser() {
        try{
            SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            User currentUser = userService.getCurrentLoginUser();
            ResponseObject<ChatRoomDto> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
            ChatRoom c = chatRoomRepo.findByUserId(currentUser.getId());
            response.setData(
                    ChatRoomDto.builder()
                            .id(c.getId())
                            .adminId(c.getAdminId())
                            .userId(c.getUserId())
                            .username(userService.findById(c.getUserId()).getUsername())
                            .latestMessage(messageService.getLatestMessageByChatRoom((Integer) c.getId()))
                            .build()
            );
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ResponseObject<List<ChatRoomDto>> findByAdmin() {
        try{
            SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            User currentUser = userService.getCurrentLoginUser();
            ResponseObject<List<ChatRoomDto>> response = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);

            response.setData(chatRoomRepo.findByAdminId(currentUser.getId()).stream().map(
                    c -> ChatRoomDto.builder()
                            .id((Integer)c.get("Id"))
                            .adminId((Integer) c.get("AdminId"))
                            .userId((Integer) c.get("UserId"))
                            .username(userService.findById((Integer) c.get("UserId")).getUsername())
                            .latestMessage(messageService.getLatestMessageByChatRoom((Integer) c.get("Id")))
                            .latestTime(smf.format((Date)c.get("LatestTime")))
                            .build()
            ).collect(Collectors.toList()));
            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException(e.getMessage());
        }
    }

    @Override
    public ChatRoom findById(Integer id) {
        return chatRoomRepo.findById(id).get();
    }

    @Override
    public ChatRoom join(Integer chatRoomId, ConnectedUser user) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ChatRoom chatRoom = chatRoomRepo.findById(chatRoomId).get();
            List<ConnectedUser> connectedUsersList = objectMapper.readValue(chatRoom.getConnectedUsers(), ArrayList.class);
            connectedUsersList.add(user);
            String connectedUsers = objectMapper.writeValueAsString(connectedUsersList);
            chatRoom.setConnectedUsers(connectedUsers);
            chatRoom = chatRoomRepo.save(chatRoom);

//            updateConnectedUsersViaWebSocket(chatRoom);
            return chatRoom;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ChatRoom leave(Integer chatRoomId, ConnectedUser user) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ChatRoom chatRoom = chatRoomRepo.findById(chatRoomId).get();
        List<LinkedHashMap<String, String>> connectedUsersList = objectMapper.readValue(chatRoom.getConnectedUsers(), ArrayList.class);
        for (LinkedHashMap<String, String> item : connectedUsersList){
            if (item.get("username").equals(user.getUsername())){
                connectedUsersList.remove(item);
                break;
            }
        }
        String connectedUsers = objectMapper.writeValueAsString(connectedUsersList);
        chatRoom.setConnectedUsers(connectedUsers);
        chatRoom = chatRoomRepo.save(chatRoom);
//        updateConnectedUsersViaWebSocket(chatRoom);
        return chatRoom;
    }



    @Override
    public void sendPublicMessage(ChatMessage message) throws JsonProcessingException {
        webSocketMessagingTemplate.convertAndSend(
                Destination.publicMessages(message.getChatRoomId()+""),
                message
        );
        messageService.appendInstantMessageToConversations(message);
    }

    @Override
    public void sendPrivateMessage(ChatMessage message) throws JsonProcessingException {
        webSocketMessagingTemplate.convertAndSendToUser(
                message.getToUser(),
                Destination.privateMessages(message.getChatRoomId()+""),
                message);

        webSocketMessagingTemplate.convertAndSendToUser(
                message.getFromUser(),
                Destination.privateMessages(message.getChatRoomId()+""),
                message);

        messageService.appendInstantMessageToConversations(message);
    }

    @Override
    public void loadOldMessage(Integer chatRoomId, String username) {
        List<ChatMessage> oldMessages = messageService.findByChatRoomId(chatRoomId);
        webSocketMessagingTemplate.convertAndSendToUser(
                username,
                Destination.oldMessages(chatRoomId+""),
                oldMessages);
    }

    @Override
    public void reloadAllChatRoom(String chatRoomId) {
        webSocketMessagingTemplate.convertAndSend(
                Destination.reloadChatRooms(),
                chatRoomId
        );
    }

    public void updateConnectedUsersViaWebSocket(ChatRoom chatRoom) throws JsonProcessingException {
        List<ConnectedUser> connectedUsers = new ObjectMapper().readValue(chatRoom.getConnectedUsers(), ArrayList.class);
        webSocketMessagingTemplate.convertAndSend(
                Destination.connectedUsers(chatRoom.getId()+""),
                connectedUsers
        );
    }
}
