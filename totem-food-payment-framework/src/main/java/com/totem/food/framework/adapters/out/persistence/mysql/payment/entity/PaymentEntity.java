package com.totem.food.framework.adapters.out.persistence.mysql.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PAYMENTS")
@Access(value= AccessType.FIELD)
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "ORDER_ID")
    private String order;

    @Column(name = "CUSTOMER_ID")
    private String customer;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "PRICE")
    private double price;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "QR_CODE_BASE64")
    private String qrcodeBase64;

    @Column(name = "EMAIL")
    private Integer email;

    @Column(name = "MODIFIED_AT")
    @UpdateTimestamp
    private ZonedDateTime modifiedAt;

    @Column(name = "CREATE_AT")
    @CreationTimestamp
    private ZonedDateTime createAt;

}
