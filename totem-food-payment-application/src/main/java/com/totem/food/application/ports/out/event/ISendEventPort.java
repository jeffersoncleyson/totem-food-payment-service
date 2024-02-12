package com.totem.food.application.ports.out.event;

public interface ISendEventPort<I, O> {

    O sendMessage(I item);
}
