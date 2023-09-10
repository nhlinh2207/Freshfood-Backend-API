package com.linh.freshfoodbackend.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tbl_city")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name", columnDefinition = "NVARCHAR(255)")
    private String name;

    @ManyToOne
    @JoinColumn(name = "CountryId")
    private Country country;
}
