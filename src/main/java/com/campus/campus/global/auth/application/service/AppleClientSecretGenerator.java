package com.campus.campus.global.auth.application.service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.campus.campus.global.auth.application.property.AppleOauthProperty;
import com.campus.campus.global.auth.exception.AppleClientSecretGenerationException;

import io.jsonwebtoken.Jwts;

@Component
public class AppleClientSecretGenerator {

	private static final String APPLE_AUDIENCE = "https://appleid.apple.com";
	private static final Duration CLIENT_SECRET_VALIDITY = Duration.ofMinutes(10);

	private final AppleOauthProperty appleOauthProperty;
	private final Clock clock;

	@Autowired
	public AppleClientSecretGenerator(AppleOauthProperty appleOauthProperty) {
		this(appleOauthProperty, Clock.systemUTC());
	}

	AppleClientSecretGenerator(AppleOauthProperty appleOauthProperty, Clock clock) {
		this.appleOauthProperty = appleOauthProperty;
		this.clock = clock;
	}

	public String generate() {
		Instant issuedAt = clock.instant();

		return Jwts.builder()
			.header()
			.keyId(appleOauthProperty.getKeyId())
			.and()
			.issuer(appleOauthProperty.getTeamId())
			.issuedAt(Date.from(issuedAt))
			.expiration(Date.from(issuedAt.plus(CLIENT_SECRET_VALIDITY)))
			.audience()
			.add(APPLE_AUDIENCE)
			.and()
			.subject(appleOauthProperty.getClientId())
			.signWith(parsePrivateKey(), Jwts.SIG.ES256)
			.compact();
	}

	private PrivateKey parsePrivateKey() {
		try {
			String privateKey = appleOauthProperty.getPrivateKey()
				.replace("\\n", "")
				.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");

			byte[] decodedKey = Base64.getDecoder().decode(privateKey);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

			return KeyFactory.getInstance("EC").generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
			throw new AppleClientSecretGenerationException();
		}
	}
}
