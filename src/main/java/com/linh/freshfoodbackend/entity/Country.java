package com.linh.freshfoodbackend.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_country")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name", columnDefinition = "NVARCHAR(255)")
    private String name;

    @OneToMany(
            mappedBy = "country",
            cascade = CascadeType.ALL
    )
    private List<City> cities;
}
