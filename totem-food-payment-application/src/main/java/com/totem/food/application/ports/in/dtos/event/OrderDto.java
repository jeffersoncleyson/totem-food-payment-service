package com.totem.food.application.ports.in.dtos.event;

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
public class OrderDto {

    @Setter
    private String id;

    @Setter
    private String customer;

    private String status;

    @Setter
    private double price;

    @Setter
    private ZonedDateTime modifiedAt;

    @Setter
    private ZonedDateTime createAt;

    @Setter
    private ZonedDateTime receivedAt;
}
