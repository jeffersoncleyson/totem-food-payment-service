package com.totem.food.framework.config;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.Arrays;

@Configuration
@Slf4j
@RequiredArgsConstructor
@Profile("!dev")
public class AWSConfiguration {

	@NonNull
	private Environment env;

	@Bean
	SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {
		
		var acknowledgementMode = AcknowledgementMode.MANUAL;
		
		var environmentDebug = new StringBuilder("\n############## [SQS Message Listener] ###############").append("\n");
		environmentDebug.append("* ActiveProfiles: ").append(Arrays.toString(env.getActiveProfiles())).append("\n");
		environmentDebug.append("* DefaultProfiles: ").append(Arrays.toString(env.getDefaultProfiles())).append("\n");
		environmentDebug.append("* AcknowledgementMode: ").append(acknowledgementMode).append("\n");
		environmentDebug.append("##############################").append("\n");
		log.info(environmentDebug.toString());

		return SqsMessageListenerContainerFactory.builder()
				.configure(options -> options.acknowledgementMode(acknowledgementMode))
				.acknowledgementResultCallback(new AckResultCallback())
				.sqsAsyncClient(sqsAsyncClient)
				.build();
	}

}
