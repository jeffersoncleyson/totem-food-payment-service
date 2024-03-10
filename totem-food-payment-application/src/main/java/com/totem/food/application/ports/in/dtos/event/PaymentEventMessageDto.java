package com.totem.food.application.ports.in.dtos.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentEventMessageDto {

    @JsonProperty("id")
    private int id;

    @JsonProperty("order")
    private String order;

    @JsonProperty("customer")
    private String customer;

    @JsonProperty("price")
    private double price;

    @JsonProperty("status")
    private String status;

    @JsonProperty("modifiedAt")
    private String modifiedAt;

    @JsonProperty("createAt")
    private String createAt;

}
