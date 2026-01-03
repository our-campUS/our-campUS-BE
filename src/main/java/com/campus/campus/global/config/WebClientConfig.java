package com.campus.campus.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient naverMapWebClient() {
		return WebClient.builder()
			.baseUrl("https://naveropenapi.apigw.ntruss.com")
			.build();
	}
}
