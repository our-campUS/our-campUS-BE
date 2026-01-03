package com.campus.campus.global.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class WebClientConfig {

	@Bean
	public RestClient restClient() {
		return RestClient.builder()
			.requestFactory(new JdkClientHttpRequestFactory(
				HttpClient.newBuilder()
					.connectTimeout(Duration.ofSeconds(10))
					.build()
			))
			.build();
	}
}
