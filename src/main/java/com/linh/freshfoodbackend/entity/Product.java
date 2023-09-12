package com.linh.freshfoodbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.linh.freshfoodbackend.utils.CustomDateSerializer;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tbl_product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name", columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(name = "Description", columnDefinition = "LONGTEXT CHARACTER SET utf8")
    private String description;

    @Column(name = "Image",  columnDefinition = "LONGTEXT CHARACTER SET utf8")
    private String image;

    @Column(name = "ExtraImage1",  columnDefinition = "LONGTEXT CHARACTER SET utf8")
    private String extra_img1;

    @Column(name = "ExtraImage2",  columnDefinition = "LONGTEXT CHARACTER SET utf8")
    private String extra_img2;

    @Column(name = "Price")
    private Integer price;

    @Column(name = "Discount")
    private Integer discount;

    @Column(name = "BuyingCount")
    private Integer buyingCount;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "Status")
    private String status;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "CreateTime")
    private Date createTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "UpdateTime")
    private Date updateTime;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;

//    @OneToMany(
//            mappedBy = "product",
//            cascade = CascadeType.ALL
//    )
//    List<CartItem> cartItems;
//
//    @OneToMany(
//            mappedBy = "product",
//            cascade = CascadeType.ALL
//    )
//    List<Rank> ranks;

    @Transient
    public String getPriceCurrency() {
        StringBuilder s = new StringBuilder(price.toString());
        for(int i = price.toString().length() - 3; i >= 0; i -= 3) {
            if(i == 0) continue;
            s.insert(i, ',');
        }
        s.append(" đ");
        return s.toString();
    }

}
