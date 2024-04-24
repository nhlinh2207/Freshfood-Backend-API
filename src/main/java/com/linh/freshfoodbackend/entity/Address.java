package com.linh.freshfoodbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.linh.freshfoodbackend.annotation.Backup;
import com.linh.freshfoodbackend.utils.CustomDateSerializer;
import com.linh.freshfoodbackend.utils.enums.AddressType;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_address")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Backup
    @Column(name = "Commune")
    private String commune;

    @Backup
    @Column(name = "District")
    private String district;

    @Backup
    @Column(name = "CityId")
    private Integer cityId;

    @Backup
    @Column(name = "CCountryId")
    private Integer countryId;

    @Backup
    @Column(name = "FullAddress")
    private String fullAddress;

    @Backup
    @Column(name = "Type")
    @Enumerated(EnumType.STRING)
    private AddressType type;

    @OneToOne
    @JoinColumn(name = "UserId")
    private User user;

    @OneToOne
    @JoinColumn(name = "CartId")
    private Cart cart;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "CreateTime")
    private Date createTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "UpdateTime")
    private Date updateTime;

}
