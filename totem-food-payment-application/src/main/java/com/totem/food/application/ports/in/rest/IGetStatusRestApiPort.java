package com.totem.food.application.ports.in.rest;

public interface IGetStatusRestApiPort<O> {

    O getByIdAndStatus(String id, String status);
}
