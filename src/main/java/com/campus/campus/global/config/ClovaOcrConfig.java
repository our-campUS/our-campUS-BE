package com.campus.campus.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClovaOcrConfig {

	@Bean
	public RestTemplate clovaOcrRestTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

		factory.setConnectTimeout(3_000); // 연결 타임아웃 3초
		factory.setReadTimeout(15_000);   // 응답 대기 타임아웃 15초

		return new RestTemplate(factory);
	}

}