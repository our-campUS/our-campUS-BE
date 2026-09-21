package com.campus.campus.global.auth.application.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.campus.campus.global.auth.application.dto.AppleTokenClaims;
import com.campus.campus.global.auth.exception.InvalidAppleIdTokenException;

@Component
public class AppleIdTokenVerifier {

	private final JwtDecoder appleIdTokenDecoder;

	public AppleIdTokenVerifier(@Qualifier("appleIdTokenDecoder") JwtDecoder appleIdTokenDecoder) {
		this.appleIdTokenDecoder = appleIdTokenDecoder;
	}

	public AppleTokenClaims verify(String identityToken, String expectedNonce) {
		try {
			Jwt jwt = appleIdTokenDecoder.decode(identityToken);

			if (!StringUtils.hasText(jwt.getSubject())
				|| !StringUtils.hasText(expectedNonce)
				|| !expectedNonce.equals(jwt.getClaimAsString("nonce"))) {
				throw new InvalidAppleIdTokenException();
			}

			return new AppleTokenClaims(jwt.getSubject(), jwt.getClaimAsString("email"));
		} catch (JwtException e) {
			throw new InvalidAppleIdTokenException();
		}
	}
}
