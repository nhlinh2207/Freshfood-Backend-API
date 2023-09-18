package com.linh.freshfoodbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.linh.freshfoodbackend.utils.CustomDateSerializer;
import com.linh.freshfoodbackend.utils.enums.OrderStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tbl_cart")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "ReceiverName")
    private String receiverName;

    @Column(name = "ReceiverPhoneNumber")
    private String receiverPhoneNumber;

    @Column(name = "ReceiverEmail")
    private String receiverEmail;

    @Column(name = "Status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "PaymentType")
    private String paymentType;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "OrderTime")
    private Date orderTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "DeliveryTime")
    private Date deliveryTime;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @OneToOne(mappedBy = "cart", cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL
    )
    private List<CartItem> cartItems;

    @ManyToOne
    @JoinColumn(name = "StaffId")
    private User staff;
}
