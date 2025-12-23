package com.campus.campus.global.util.jwt.application.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

	private final RedisTemplate<String, Object> redisTemplate;

	// Refresh Token 저장
	public void setRefreshToken(String role, String id, String refreshToken, long expirationSeconds) {
		String key = "RT:" + role + ":" + id;
		redisTemplate.opsForValue().set(key, refreshToken, expirationSeconds, TimeUnit.SECONDS);
	}

	// Refresh Token 가져오기
	public String getRefreshToken(String role, String id) {
		String key = "RT:" + role + ":" + id;
		return (String) redisTemplate.opsForValue().get(key);
	}

	// Refresh Token 삭제
	public void deleteRefreshToken(String role, String id) {
		String key = "RT:" + role + ":" + id;
		redisTemplate.delete(key);
	}

	// BlackList 추가 (Key: accessToken, Value: "logout")
	public void setBlackList(String accessToken, String msg, Long milliSeconds) {
		redisTemplate.opsForValue().set(accessToken, msg, milliSeconds, TimeUnit.MILLISECONDS);
	}

	// BlackList 확인
	public boolean hasKeyBlackList(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}
}
