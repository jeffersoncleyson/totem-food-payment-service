package com.totem.food.application.ports.in.dtos.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class AWSMessage {

	@JsonProperty("Type")
	private String type;

	@JsonProperty("MessageId")
	private String messageId;

	@JsonProperty("TopicArn")
	private String topicArn;

	@JsonProperty("Message")
	private String message;

	@JsonProperty("Timestamp")
	private Date timestamp;

	@JsonProperty("SignatureVersion")
	private String signatureVersion;

	@JsonProperty("Signature")
	private String signature;

	@JsonProperty("SigningCertURL")
	private String signingCertURL;

	@JsonProperty("UnsubscribeURL")
	private String unsubscribeURL;

	@JsonProperty("MessageAttributes")
	private Map<String, MessageValue> messageAttributes;
}
