package com.campus.campus.global.auth.application.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoOauthProperty {

	private String clientId;
	private String clientSecret;
	private String redirectUri;
	private String adminKey;
}
