package com.totem.food.application.ports.in.rest;

public interface ISearchImageApiPort<O> {

    O getImage(Integer id, boolean condition);
}
