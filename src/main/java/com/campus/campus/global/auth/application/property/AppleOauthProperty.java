package com.campus.campus.global.auth.application.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "oauth.apple")
public class AppleOauthProperty {

	@NotBlank
	private String clientId;

	@NotBlank
	private String teamId;

	@NotBlank
	private String keyId;

	@NotBlank
	private String privateKey;

}
