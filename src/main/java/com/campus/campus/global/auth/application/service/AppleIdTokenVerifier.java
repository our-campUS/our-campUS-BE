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

	/**
	 * Apple ID Token의 서명 및 표준 claim을 검증하고, 전달받은 SHA-256 해시 nonce와 일치하는지 확인한다.
	 *
	 * @param identityToken Apple에서 발급한 ID Token
	 * @param expectedHashedNonce 로그인 API로 전달된 SHA-256 해시 nonce
	 * @return 검증된 Apple 사용자 claim
	 */
	public AppleTokenClaims verify(String identityToken, String expectedHashedNonce) {
		try {
			Jwt jwt = appleIdTokenDecoder.decode(identityToken);

			if (!StringUtils.hasText(jwt.getSubject())
				|| !StringUtils.hasText(expectedHashedNonce)
				|| !expectedHashedNonce.equals(jwt.getClaimAsString("nonce"))) {
				throw new InvalidAppleIdTokenException();
			}

			return new AppleTokenClaims(jwt.getSubject(), jwt.getClaimAsString("email"));
		} catch (JwtException e) {
			throw new InvalidAppleIdTokenException();
		}
	}
}
