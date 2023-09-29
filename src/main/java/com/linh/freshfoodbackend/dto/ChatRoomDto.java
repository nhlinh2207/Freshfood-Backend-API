package com.linh.freshfoodbackend.dto;

import lombok.*;

import java.util.Date;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {

    private Integer id;
    private Integer userId;
    private String username;
    private Integer adminId;
    private String connectedUsers;
    private String latestMessage;
    private String latestTime;
}
