package com.totem.food.application.ports.in.dtos.event;

import lombok.Data;

import java.util.Map;

@Data
public class MessageAttributes {

	private Map<String, MessageValue> values;
}
