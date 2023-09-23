package com.linh.freshfoodbackend.config.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linh.freshfoodbackend.entity.ConnectedUser;
import com.linh.freshfoodbackend.service.IChatRoomService;
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

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event){
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = accessor.getNativeHeader("chatRoomId").get(0);
        System.out.println("connect");
        accessor.getSessionAttributes().put("chatRoomId", chatRoomId);
        System.out.println(event.getUser());
        ConnectedUser joiningUser = new ConnectedUser(event.getUser().getName(),new Date());
        chatRoomService.join(Integer.valueOf(chatRoomId), joiningUser);
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) throws JsonProcessingException {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = headers.getSessionAttributes().get("chatRoomId").toString();
        ConnectedUser leavingUser = new ConnectedUser(event.getUser().getName());
        System.out.println("Socket disconnected");
        chatRoomService.leave(Integer.valueOf(chatRoomId), leavingUser);
    }

}
