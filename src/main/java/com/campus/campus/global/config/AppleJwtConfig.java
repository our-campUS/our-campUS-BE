package com.campus.campus.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.campus.campus.global.auth.application.property.AppleOauthProperty;
import com.campus.campus.global.auth.application.service.AppleAudienceValidator;

@Configuration
public class AppleJwtConfig {

	private static final String APPLE_ISSUER = "https://appleid.apple.com";
	private static final String APPLE_JWK_SET_URI = "https://appleid.apple.com/auth/keys";

	@Bean
	@Qualifier("appleIdTokenDecoder")
	public JwtDecoder appleIdTokenDecoder(AppleOauthProperty appleOauthProperty) {
		NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(APPLE_JWK_SET_URI).build();

		OAuth2TokenValidator<Jwt> issuerAndTimestampValidator =
			JwtValidators.createDefaultWithIssuer(APPLE_ISSUER);
		OAuth2TokenValidator<Jwt> audienceValidator =
			new AppleAudienceValidator(appleOauthProperty.getClientId());

		jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
			issuerAndTimestampValidator,
			audienceValidator
		));

		return jwtDecoder;
	}
}
