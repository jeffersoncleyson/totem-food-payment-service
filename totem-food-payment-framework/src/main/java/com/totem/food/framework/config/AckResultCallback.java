package com.totem.food.framework.config;

import io.awspring.cloud.sqs.listener.acknowledgement.AsyncAcknowledgementResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class AckResultCallback implements AsyncAcknowledgementResultCallback<Object> {

	@Override
	public CompletableFuture<Void> onFailure(Collection<Message<Object>> messages, Throwable t) {
		log.error("Something was wrong to process messages: \n"+messages, t);
		return AsyncAcknowledgementResultCallback.super.onFailure(messages, t);
	}

	@Override
	public CompletableFuture<Void> onSuccess(Collection<Message<Object>> messages) {
		log.trace("Success to process messages:\n{}", messages);
		return AsyncAcknowledgementResultCallback.super.onSuccess(messages);
	}

}
