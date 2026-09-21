package com.campus.campus.global.auth.application.service;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

class AppleAudienceValidatorTest {

	private static final String CLIENT_ID = "com.campus.app";

	private final AppleAudienceValidator validator = new AppleAudienceValidator(CLIENT_ID);

	@Test
	void validate_클라이언트_ID가_audience에_포함되면_검증에_성공한다() {
		Jwt jwt = createJwt(List.of(CLIENT_ID));

		OAuth2TokenValidatorResult result = validator.validate(jwt);

		assertThat(result.hasErrors()).isFalse();
	}

	@Test
	void validate_클라이언트_ID가_audience에_없으면_검증에_실패한다() {
		Jwt jwt = createJwt(List.of("another-client"));

		OAuth2TokenValidatorResult result = validator.validate(jwt);

		assertThat(result.hasErrors()).isTrue();
	}

	@Test
	void validate_audience가_없으면_검증에_실패한다() {
		Jwt jwt = createJwt(null);

		OAuth2TokenValidatorResult result = validator.validate(jwt);

		assertThat(result.hasErrors()).isTrue();
	}

	private Jwt createJwt(List<String> audience) {
		Instant now = Instant.now();

		Jwt.Builder jwtBuilder = Jwt.withTokenValue("identity-token")
			.header("alg", "RS256")
			.subject("apple-user-id")
			.issuedAt(now)
			.expiresAt(now.plusSeconds(300));

		if (audience != null) {
			jwtBuilder.audience(audience);
		}

		return jwtBuilder.build();
	}
}
