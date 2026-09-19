package com.campus.campus.global.auth.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import com.campus.campus.global.auth.application.dto.AppleTokenClaims;
import com.campus.campus.global.auth.exception.InvalidAppleIdTokenException;

@ExtendWith(MockitoExtension.class)
class AppleIdTokenVerifierTest {

	@Mock
	private JwtDecoder jwtDecoder;

	@Test
	void verify_검증된_토큰에서_애플_ID와_이메일을_반환한다() {
		AppleIdTokenVerifier verifier = new AppleIdTokenVerifier(jwtDecoder);
		Jwt jwt = createJwt("apple-user-id", "user@example.com");
		when(jwtDecoder.decode("identity-token")).thenReturn(jwt);

		AppleTokenClaims claims = verifier.verify("identity-token");

		assertThat(claims.appleId()).isEqualTo("apple-user-id");
		assertThat(claims.email()).isEqualTo("user@example.com");
	}

	@Test
	void verify_토큰_검증에_실패하면_예외를_던진다() {
		AppleIdTokenVerifier verifier = new AppleIdTokenVerifier(jwtDecoder);
		when(jwtDecoder.decode("invalid-token")).thenThrow(new BadJwtException("invalid token"));

		assertThatThrownBy(() -> verifier.verify("invalid-token"))
			.isInstanceOf(InvalidAppleIdTokenException.class);
	}

	@Test
	void verify_subject가_없으면_예외를_던진다() {
		AppleIdTokenVerifier verifier = new AppleIdTokenVerifier(jwtDecoder);
		Jwt jwt = createJwt(null, "user@example.com");
		when(jwtDecoder.decode("identity-token")).thenReturn(jwt);

		assertThatThrownBy(() -> verifier.verify("identity-token"))
			.isInstanceOf(InvalidAppleIdTokenException.class);
	}

	private Jwt createJwt(String subject, String email) {
		Instant now = Instant.now();
		Jwt.Builder jwtBuilder = Jwt.withTokenValue("identity-token")
			.header("alg", "RS256")
			.claim("email", email)
			.issuedAt(now)
			.expiresAt(now.plusSeconds(300));

		if (subject != null) {
			jwtBuilder.subject(subject);
		}

		return jwtBuilder.build();
	}
}
