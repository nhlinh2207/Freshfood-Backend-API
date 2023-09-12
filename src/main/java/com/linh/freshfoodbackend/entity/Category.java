package com.linh.freshfoodbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_category")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name", columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(name = "Description", columnDefinition = "LONGTEXT CHARACTER SET utf8")
    private String description;

    @Column(name = "Status")
    private String status;

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    List<Product> products;
}
