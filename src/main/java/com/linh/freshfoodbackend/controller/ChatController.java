package com.linh.freshfoodbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linh.freshfoodbackend.entity.ChatMessage;
import com.linh.freshfoodbackend.entity.ChatRoom;
import com.linh.freshfoodbackend.entity.ConnectedUser;
import com.linh.freshfoodbackend.service.IChatRoomService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class ChatController {

    private final IChatRoomService chatRoomService;

    @Secured("ROLE_ADMIN")
    @PostMapping(path = "/chatroom")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.CREATED)
    public ChatRoom createChatRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.save(chatRoom);
    }

    @RequestMapping("/chatroom/{chatRoomId}")
    public ModelAndView join(@PathVariable String chatRoomId, Principal principal) {
        ModelAndView modelAndView = new ModelAndView("chatroom");
        modelAndView.addObject("chatRoom", chatRoomService.findById(Integer.valueOf(chatRoomId)));
        return modelAndView;
    }

    @SubscribeMapping("/connected.users")
    public List<ConnectedUser> listChatRoomConnectedUsersOnSubscribe(SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {
        String chatRoomId = headerAccessor.getSessionAttributes().get("chatRoomId").toString();
        ChatRoom chatRoom = chatRoomService.findById(Integer.valueOf(chatRoomId));
        return new ObjectMapper().readValue(chatRoom.getConnectedUsers(), ArrayList.class);
    }

    @MessageMapping("/old.messages")
    public void listOldMessagesFromUserOnSubscribe(Principal principal, SimpMessageHeaderAccessor headerAccessor) {
        String chatRoomId = headerAccessor.getSessionAttributes().get("chatRoomId").toString();
        chatRoomService.loadOldMessage(Integer.valueOf(chatRoomId), principal.getName());
    }

    @MessageMapping("/send.message")
    public void sendMessage(@Payload ChatMessage instantMessage, Principal principal,
                            SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {
        String chatRoomId = headerAccessor.getSessionAttributes().get("chatRoomId").toString();
        instantMessage.setFromUser(principal.getName());
        instantMessage.setSenderType(instantMessage.getSenderType());
        instantMessage.setChatRoomId(Integer.valueOf(chatRoomId));
        chatRoomService.sendPublicMessage(instantMessage);
    }

}
