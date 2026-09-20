package com.campus.campus.global.auth.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class AppleNonceServiceTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;
	@Mock
	private ValueOperations<String, Object> valueOperations;

	@Test
	void issue_일회성_nonce를_Redis에_5분간_저장한다() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		AppleNonceService appleNonceService = new AppleNonceService(redisTemplate);

		String nonce = appleNonceService.issue();

		assertThat(nonce).isNotBlank();
		verify(valueOperations).set(
			eq("APPLE_LOGIN_NONCE:" + nonce),
			eq(Boolean.TRUE),
			eq(5L),
			eq(TimeUnit.MINUTES)
		);
	}

	@Test
	void consume_저장된_nonce는_한번만_소비한다() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.getAndDelete("APPLE_LOGIN_NONCE:nonce")).thenReturn(Boolean.TRUE);
		AppleNonceService appleNonceService = new AppleNonceService(redisTemplate);

		boolean consumed = appleNonceService.consume("nonce");

		assertThat(consumed).isTrue();
	}

	@Test
	void consume_없거나_이미_소비한_nonce는_false를_반환한다() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.getAndDelete("APPLE_LOGIN_NONCE:nonce")).thenReturn(null);
		AppleNonceService appleNonceService = new AppleNonceService(redisTemplate);

		boolean consumed = appleNonceService.consume("nonce");

		assertThat(consumed).isFalse();
	}
}
