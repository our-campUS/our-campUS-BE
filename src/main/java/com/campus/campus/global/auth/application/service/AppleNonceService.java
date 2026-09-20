package com.campus.campus.global.auth.application.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppleNonceService {

	private static final String NONCE_KEY_PREFIX = "APPLE_LOGIN_NONCE:";
	private static final int NONCE_TTL_MINUTES = 5;
	private static final int NONCE_BYTE_LENGTH = 32;

	private final RedisTemplate<String, Object> redisTemplate;
	private final SecureRandom secureRandom = new SecureRandom();

	public String issue() {
		byte[] nonceBytes = new byte[NONCE_BYTE_LENGTH];
		secureRandom.nextBytes(nonceBytes);

		String nonce = Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes);
		redisTemplate.opsForValue().set(
			key(nonce),
			Boolean.TRUE,
			NONCE_TTL_MINUTES,
			TimeUnit.MINUTES
		);

		return nonce;
	}

	public boolean consume(String nonce) {
		return redisTemplate.opsForValue().getAndDelete(key(nonce)) != null;
	}

	private String key(String nonce) {
		return NONCE_KEY_PREFIX + nonce;
	}
}
