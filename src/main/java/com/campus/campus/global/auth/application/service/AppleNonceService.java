package com.campus.campus.global.auth.application.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	private static final String NONCE_VALUE = "issued";

	private final RedisTemplate<String, String> redisTemplate;
	private final SecureRandom secureRandom = new SecureRandom();

	/**
	 * Apple 로그인 요청에 사용할 일회성 nonce 원문을 발급하고, SHA-256 해시값을 Redis에 저장한다.
	 *
	 * @return 클라이언트가 Apple 로그인 요청에 사용할 nonce 원문
	 */
	public String issue() {
		byte[] nonceBytes = new byte[NONCE_BYTE_LENGTH];
		secureRandom.nextBytes(nonceBytes);

		String nonce = Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes);
		String hashedNonce = sha256(nonce);
		redisTemplate.opsForValue().set(
			key(hashedNonce),
			NONCE_VALUE,
			NONCE_TTL_MINUTES,
			TimeUnit.MINUTES
		);

		return nonce;
	}

	/**
	 * SHA-256 해시 nonce를 원자적으로 조회 및 삭제하여 재사용을 방지한다.
	 *
	 * @param hashedNonce 소비할 SHA-256 해시 nonce
	 * @return 저장된 nonce가 존재하여 정상 소비된 경우 {@code true}
	 */
	public boolean consume(String hashedNonce) {
		return redisTemplate.opsForValue().getAndDelete(key(hashedNonce)) != null;
	}

	/**
	 * nonce 원문을 Apple 로그인 요청에 사용할 SHA-256 16진수 문자열로 변환한다.
	 *
	 * @param nonce 원문 nonce
	 * @return 소문자 16진수 SHA-256 해시값
	 */
	static String sha256(String nonce) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256")
				.digest(nonce.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			return java.util.HexFormat.of().formatHex(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 algorithm is unavailable", e);
		}
	}

	private String key(String nonce) {
		return NONCE_KEY_PREFIX + nonce;
	}
}
