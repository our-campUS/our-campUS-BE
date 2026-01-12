package com.campus.campus.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClovaOcrConfig {

	@Bean
	public RestTemplate clovaOcrRestTemplate() {
		return new RestTemplate();
	}
}
