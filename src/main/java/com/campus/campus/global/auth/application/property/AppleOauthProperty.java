package com.campus.campus.global.auth.application.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth.apple")
public class AppleOauthProperty {

	private String clientId;
	private String teamId;
	private String keyId;
	private String privateKey;
}
