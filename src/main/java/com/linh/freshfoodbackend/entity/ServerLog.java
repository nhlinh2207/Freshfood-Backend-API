package com.linh.freshfoodbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.linh.freshfoodbackend.utils.CustomDateSerializer;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_server_log")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServerLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Code")
    private String code;

    @Column(name = "Type")
    private String type;

    @Column(name = "Ip")
    private String ip;

    @Column(name = "Actor")
    private String actor;

    @Nationalized
    @Column(name = "OldValue")
    private String oldValue;

    @Nationalized
    @Column(name = "Content")
    private String content;

    @Nationalized
    @Column(name = "Reason")
    private String reason;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "CreateTime")
    private Date createTime;
}
