package com.totem.food.framework.config;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.endpoints.SnsEndpointParams;
import software.amazon.awssdk.services.sns.endpoints.internal.DefaultSnsEndpointProvider;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

@Configuration
@Slf4j
@RequiredArgsConstructor
@Profile("dev")
public class AWSDevConfiguration {

	@NonNull
	private Environment env;

	@Value("${localstack.endpoint}")
	private String endpoint;

	@Value("${localstack.region}")
	private String region;

	@Value("${localstack.accessKey}")
	private String accessKey;

	@Value("${localstack.secretKey}")
	private String secretKey;
	
	@Bean
	SnsClient snsClient(AwsCredentialsProvider awsCredentialsProvider) throws URISyntaxException {
		
		var snsDebug = new StringBuilder("\n############## [SNS] ###############").append("\n");
		snsDebug.append("* ActiveProfiles: ").append(Arrays.toString(env.getActiveProfiles())).append("\n");
		snsDebug.append("* DefaultProfiles: ").append(Arrays.toString(env.getDefaultProfiles())).append("\n");
		snsDebug.append("* SnsAsyncClient [serviceEndpoint]: ").append(endpoint).append("\n");
		snsDebug.append("* SnsAsyncClient [signingRegion]: ").append(region).append("\n");
		snsDebug.append("* SnsAsyncClient [CredentialsProvider]: ").append("AwsBasicCredentials").append("\n");
		snsDebug.append("##############################").append("\n");
		log.debug(snsDebug.toString());
		
		var targetAwsUri = new URI(endpoint);
		var defaultSnsEndpointProvider = new DefaultSnsEndpointProvider();
		defaultSnsEndpointProvider.resolveEndpoint(SnsEndpointParams.builder()
				.endpoint(endpoint)
				.region(Region.of(region))
				.build());
		return SnsClient.builder()
				.endpointOverride(targetAwsUri)
				.endpointProvider(defaultSnsEndpointProvider)
				.region(Region.of(region))
				.credentialsProvider(awsCredentialsProvider)
				.build();
	}

	@Bean
	AwsCredentialsProvider awsCredentialsProvider() {
		return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
	}

	@Bean
	SqsAsyncClient sqsAsyncClient() throws URISyntaxException {
		var snsDebug = new StringBuilder("\n*****************************").append("\n");
		snsDebug.append("* ActiveProfiles: ").append(Arrays.toString(env.getActiveProfiles())).append("\n");
		snsDebug.append("* DefaultProfiles: ").append(Arrays.toString(env.getDefaultProfiles())).append("\n");
		snsDebug.append("* SqsAsyncClient [serviceEndpoint]: ").append(endpoint).append("\n");
		snsDebug.append("* SqsAsyncClient [signingRegion]: ").append(region).append("\n");
		snsDebug.append("* SqsAsyncClient [CredentialsProvider]: ").append("AwsBasicCredentials").append("\n");
		snsDebug.append("*****************************").append("\n");
		log.debug(snsDebug.toString());
		var targetAwsUri = new URI(endpoint);
		var staticCredentialsProvider = StaticCredentialsProvider
				.create(AwsBasicCredentials.create(accessKey, secretKey));
		return SqsAsyncClient//
				.builder()//
				.region(Region.of(region))//
				.endpointOverride(targetAwsUri)//
				.credentialsProvider(staticCredentialsProvider)//
				.build();//
	}

	@Bean
	SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {

		log.debug("Configuring AcknowledgementMode.MANUAL for sqs messages receiveds.");
		return SqsMessageListenerContainerFactory.builder()
				.configure(options -> options.acknowledgementMode(AcknowledgementMode.MANUAL))
				.acknowledgementResultCallback(new AckResultCallback())
				.sqsAsyncClient(sqsAsyncClient)
				.build();
	}
	
}
