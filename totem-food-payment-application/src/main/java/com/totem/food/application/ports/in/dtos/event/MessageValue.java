package com.totem.food.application.ports.in.dtos.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageValue {

	@JsonProperty("Type")
	private String type;

	@JsonProperty("Value")
	private String value;
}
