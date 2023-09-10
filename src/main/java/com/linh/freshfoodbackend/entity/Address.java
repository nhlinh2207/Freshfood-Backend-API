package com.linh.freshfoodbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

    @Column(name = "Commune")
    private String commune;

    @Column(name = "District")
    private String district;

    @Column(name = "CityId")
    private Integer cityId;

    @Column(name = "CCountryId")
    private Integer countryId;

    @Column(name = "FullAddress")
    private String fullAddress;

    @Column(name = "Type")
    @Enumerated(EnumType.STRING)
    private AddressType type;

    @OneToOne
    @JoinColumn(name = "UserId")
    private User user;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "CreateTime")
    private Date createTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "UpdateTime")
    private Date updateTime;

}
