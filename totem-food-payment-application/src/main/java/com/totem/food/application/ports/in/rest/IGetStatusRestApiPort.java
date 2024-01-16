package com.totem.food.application.ports.in.rest;

public interface IGetStatusRestApiPort<O> {

    O update(String id, String status);
}
