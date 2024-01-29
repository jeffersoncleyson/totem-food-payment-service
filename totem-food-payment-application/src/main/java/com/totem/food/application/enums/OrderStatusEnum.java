package com.totem.food.application.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    NEW("NEW"),
    WAITING_PAYMENT("WAITING_PAYMENT"),
    RECEIVED("RECEIVED"),
    IN_PREPARATION("IN_PREPARATION"),
    READY("READY"),
    FINALIZED("FINALIZED"),
    CANCELED("CANCELED");

    public final String key;

    OrderStatusEnum(String key) {
        this.key = key;
    }
}
