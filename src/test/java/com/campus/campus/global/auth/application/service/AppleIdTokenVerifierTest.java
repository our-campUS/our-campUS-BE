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

	private static final String HASHED_NONCE =
		"78377b525757b494427f89014f97d79928f3938d14eb51e20fb5dec9834eb304";

	@Mock
	private JwtDecoder jwtDecoder;

	/** ID Token의 해시 nonce가 로그인 API의 해시 nonce와 일치할 때 사용자 claim을 반환하는지 검증한다. */
	@Test
	void verify_검증된_토큰에서_애플_ID와_이메일을_반환한다() {
		AppleIdTokenVerifier verifier = new AppleIdTokenVerifier(jwtDecoder);
		Jwt jwt = createJwt("apple-user-id", "user@example.com", HASHED_NONCE);
		when(jwtDecoder.decode("identity-token")).thenReturn(jwt);

		AppleTokenClaims claims = verifier.verify("identity-token", HASHED_NONCE);

		assertThat(claims.appleId()).isEqualTo("apple-user-id");
		assertThat(claims.email()).isEqualTo("user@example.com");
	}

	/** JWT decoder가 토큰을 거부하면 Apple ID Token 검증 예외로 변환하는지 검증한다. */
	@Test
	void verify_토큰_검증에_실패하면_예외를_던진다() {
		AppleIdTokenVerifier verifier = new AppleIdTokenVerifier(jwtDecoder);
		when(jwtDecoder.decode("invalid-token")).thenThrow(new BadJwtException("invalid token"));

		assertThatThrownBy(() -> verifier.verify("invalid-token", "nonce"))
			.isInstanceOf(InvalidAppleIdTokenException.class);
	}

	/** subject claim이 없는 ID Token을 거부하는지 검증한다. */
	@Test
	void verify_subject가_없으면_예외를_던진다() {
		AppleIdTokenVerifier verifier = new AppleIdTokenVerifier(jwtDecoder);
		Jwt jwt = createJwt(null, "user@example.com", HASHED_NONCE);
		when(jwtDecoder.decode("identity-token")).thenReturn(jwt);

		assertThatThrownBy(() -> verifier.verify("identity-token", HASHED_NONCE))
			.isInstanceOf(InvalidAppleIdTokenException.class);
	}

	/** ID Token과 요청의 해시 nonce가 다르면 로그인 요청을 거부하는지 검증한다. */
	@Test
	void verify_nonce가_일치하지_않으면_예외를_던진다() {
		AppleIdTokenVerifier verifier = new AppleIdTokenVerifier(jwtDecoder);
		Jwt jwt = createJwt("apple-user-id", "user@example.com", "token-nonce");
		when(jwtDecoder.decode("identity-token")).thenReturn(jwt);

		assertThatThrownBy(() -> verifier.verify("identity-token", HASHED_NONCE))
			.isInstanceOf(InvalidAppleIdTokenException.class);
	}

	private Jwt createJwt(String subject, String email, String nonce) {
		Instant now = Instant.now();
		Jwt.Builder jwtBuilder = Jwt.withTokenValue("identity-token")
			.header("alg", "RS256")
			.claim("email", email)
			.claim("nonce", nonce)
			.issuedAt(now)
			.expiresAt(now.plusSeconds(300));

		if (subject != null) {
			jwtBuilder.subject(subject);
		}

		return jwtBuilder.build();
	}
}
