package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.service.IChatRoomService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/chatroom")
public class ChatRoomController {

    private final IChatRoomService chatRoomService;

    @GetMapping(path = "/findByUser")
    public ResponseEntity<?> findByUser(){
        return ResponseEntity.ok(chatRoomService.findByUser());
    }

    @GetMapping(path = "/findByAdmin")
    public ResponseEntity<?> findByAdmin(){
        return ResponseEntity.ok(chatRoomService.findByAdmin());
    }

}
