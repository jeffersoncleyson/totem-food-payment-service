package com.totem.food.application.ports.in.rest;

public interface IUpdateStatusByIdRestApiPort<O> {

    O update(String id);
}
