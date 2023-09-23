package com.linh.freshfoodbackend.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tbl_chatroom")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "AdminId")
    private Integer adminId;

    @Column(name = "UserId")
    private Integer userId;

    @Column(name = "ConnectedUsers", columnDefinition = "LONGTEXT CHARACTER SET utf8")
    private String connectedUsers;

}
