package com.linh.freshfoodbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.linh.freshfoodbackend.utils.CustomDateSerializer;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "tbl_token_device")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "AndroidToken")
    private String androidToken;

    @Column(name = "DesktopToken")
    private String desktopToken;

    @Column(name = "IOSToken")
    private String iosToken;

    @Column(name = "WebToken")
    private String webToken;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "CreateTime")
    private Date createTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "UpdateTime")
    private Date updateTime;

    @OneToOne
    @JoinColumn(name = "UserId")
    private User user;

    public List<String> getAllTokens() {
        List<String> result = new ArrayList<>();
        result.add(this.getWebToken());
        result.add(this.getAndroidToken());
        result.add(this.getDesktopToken());
        result.add(this.getIosToken());
        result = result.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return result;
    }

    @Override
    public String toString() {
        return "TokenDevice{" +
                "deviceId=" + id +
                ", webToken='" + webToken + '\'' +
                ", iosToken='" + iosToken + '\'' +
                ", androidToken='" + androidToken + '\'' +
                ", desktopToken='" + desktopToken + '\'' +
                '}';
    }
}
