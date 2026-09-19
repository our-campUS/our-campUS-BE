package com.campus.campus.global.auth.application.service;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AppleAudienceValidator implements OAuth2TokenValidator<Jwt> {

	private static final OAuth2Error INVALID_AUDIENCE = new OAuth2Error(
		"invalid_token",
		"Apple ID Token에 유효한 audience가 없습니다.",
		null
	);

	private final String clientId;

	public AppleAudienceValidator(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public OAuth2TokenValidatorResult validate(Jwt token) {
		if (token.getAudience() != null && token.getAudience().contains(clientId)) {
			return OAuth2TokenValidatorResult.success();
		}

		return OAuth2TokenValidatorResult.failure(INVALID_AUDIENCE);
	}
}
