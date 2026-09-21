package com.campus.campus.global.auth.application.service;

import static org.assertj.core.api.Assertions.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.campus.global.auth.application.property.AppleOauthProperty;
import com.campus.campus.global.auth.exception.AppleClientSecretGenerationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

class AppleClientSecretGeneratorTest {

	private static final Instant NOW = Instant.parse("2026-09-20T00:00:00Z");

	private AppleOauthProperty property;

	@BeforeEach
	void setUp() {
		property = new AppleOauthProperty();
		property.setClientId("com.campus.app");
		property.setTeamId("TEAMID1234");
		property.setKeyId("KEYID12345");
	}

	@Test
	void generate_Apple_규격의_ES256_client_secret을_생성한다() throws Exception {
		KeyPair keyPair = createKeyPair();
		property.setPrivateKey(toPem(keyPair));
		AppleClientSecretGenerator generator = new AppleClientSecretGenerator(
			property,
			Clock.fixed(NOW, ZoneOffset.UTC)
		);

		String clientSecret = generator.generate();
		Jws<Claims> parsed = Jwts.parser()
			.verifyWith((ECPublicKey)keyPair.getPublic())
			.clock(() -> java.util.Date.from(NOW))
			.build()
			.parseSignedClaims(clientSecret);

		assertThat(parsed.getHeader().getKeyId()).isEqualTo("KEYID12345");
		assertThat(parsed.getHeader().getAlgorithm()).isEqualTo("ES256");
		assertThat(parsed.getPayload().getIssuer()).isEqualTo("TEAMID1234");
		assertThat(parsed.getPayload().getSubject()).isEqualTo("com.campus.app");
		assertThat(parsed.getPayload().getAudience()).containsExactly("https://appleid.apple.com");
		assertThat(parsed.getPayload().getIssuedAt().toInstant()).isEqualTo(NOW);
		assertThat(parsed.getPayload().getExpiration().toInstant()).isEqualTo(NOW.plusSeconds(600));
	}

	@Test
	void generate_유효하지_않은_private_key면_예외를_던진다() {
		property.setPrivateKey("invalid-private-key");
		AppleClientSecretGenerator generator = new AppleClientSecretGenerator(
			property,
			Clock.fixed(NOW, ZoneOffset.UTC)
		);

		assertThatThrownBy(generator::generate)
			.isInstanceOf(AppleClientSecretGenerationException.class);
	}

	private KeyPair createKeyPair() throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
		generator.initialize(new ECGenParameterSpec("secp256r1"));
		return generator.generateKeyPair();
	}

	private String toPem(KeyPair keyPair) {
		String encodedKey = Base64.getMimeEncoder(64, "\n".getBytes())
			.encodeToString(keyPair.getPrivate().getEncoded());
		return "-----BEGIN PRIVATE KEY-----\n" + encodedKey + "\n-----END PRIVATE KEY-----";
	}
}
