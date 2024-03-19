package com.totem.food.application.ports.in.event;

public interface IReceiveEventPort<I, O> {

    O receiveMessage(I item);
}
