package com.linh.freshfoodbackend.entity;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_role")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Name", columnDefinition = "VARCHAR(255)")
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "tbl_role_permission",
            joinColumns =@JoinColumn(name = "RoleId"),
            inverseJoinColumns = @JoinColumn(name = "PermissionId")
    )
    private Set<Permission> permissions;
}
