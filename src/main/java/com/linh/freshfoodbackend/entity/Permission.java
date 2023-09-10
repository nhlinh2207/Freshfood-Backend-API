package com.linh.freshfoodbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.linh.freshfoodbackend.utils.CustomDateSerializer;
import lombok.*;

import javax.persistence.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "tbl_permission")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer permissionId;

    @Column(name = "ActionCode", nullable = false, unique = true)
    private String actionCode;

    @Column(name = "ActionName", nullable = false, unique = true)
    private String actionName;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
}
