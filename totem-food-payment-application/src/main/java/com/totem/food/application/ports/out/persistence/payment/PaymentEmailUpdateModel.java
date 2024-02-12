package com.totem.food.application.ports.out.persistence.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEmailUpdateModel {

    private Integer id;
    private Integer email;

}
