package com.campus.campus.domain.place.infrastructure.naver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NaverMapConfig {

	@Value("${map.naver.client-id}")
	public String clientId;

	@Value("${map.naver.client-secret}")
	public String clientSecret;
}
