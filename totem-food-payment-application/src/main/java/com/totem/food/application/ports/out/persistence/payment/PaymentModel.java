package com.totem.food.application.ports.out.persistence.payment;

import com.totem.food.domain.payment.PaymentDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentModel {

    private String id;
    //@todo - refact private OrderDomain order;
    //@todo - refact private CustomerDomain customer;
    private double price;
    private String token;

    @Setter
    private String qrcodeBase64;

    @Builder.Default
    private PaymentDomain.PaymentStatus status = PaymentDomain.PaymentStatus.PENDING;
    private ZonedDateTime modifiedAt;
    private ZonedDateTime createAt;

}
