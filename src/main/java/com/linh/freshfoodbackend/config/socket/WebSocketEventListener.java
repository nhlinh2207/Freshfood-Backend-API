package com.linh.freshfoodbackend.config.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linh.freshfoodbackend.entity.ConnectedUser;
import com.linh.freshfoodbackend.service.IChatRoomService;
import com.linh.freshfoodbackend.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Date;

@Component
@AllArgsConstructor
public class WebSocketEventListener {

    private final IChatRoomService chatRoomService;
    private final IUserService userService;

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event){
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = accessor.getNativeHeader("chatRoomId").get(0);
        String currentUserEmail = accessor.getNativeHeader("email").get(0);
        accessor.getSessionAttributes().put("chatRoomId", chatRoomId);
        accessor.getSessionAttributes().put("email", currentUserEmail);
        ConnectedUser joiningUser = new ConnectedUser(event.getUser().getName(), currentUserEmail ,new Date());
        chatRoomService.join(Integer.valueOf(chatRoomId), joiningUser);
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) throws JsonProcessingException {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = headers.getSessionAttributes().get("chatRoomId").toString();
        String currentUserEmail = headers.getSessionAttributes().get("email").toString();
        ConnectedUser leavingUser = new ConnectedUser(event.getUser().getName(), currentUserEmail ,new Date());
        chatRoomService.leave(Integer.valueOf(chatRoomId), leavingUser);
    }

}
