package com.linh.freshfoodbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.linh.freshfoodbackend.utils.CustomDateSerializer;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_chat_message")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "FromUser")
    private String fromUser;

    @Column(name = "ToUser")
    private String toUser;

    @Column(name = "Username")
    private String username;

    @Column(name = "SenderType")
    private String senderType;

    @Column(name = "ChatRoomId")
    private Integer chatRoomId;

    @Column(name = "Content")
    private String content;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "CreateTime")
    private Date createTime;

    public boolean isPublic() {
        return this.toUser == null;
    }

//    public boolean isFromAdmin() {
//        return this.fromUser.equals(SystemUsers.ADMIN.getUsername());
//    }

}
