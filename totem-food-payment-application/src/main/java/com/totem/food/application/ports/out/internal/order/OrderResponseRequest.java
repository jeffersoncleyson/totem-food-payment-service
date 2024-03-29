package com.totem.food.application.ports.out.internal.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseRequest {

    private String id;
    private String status;
    private double price;
}
