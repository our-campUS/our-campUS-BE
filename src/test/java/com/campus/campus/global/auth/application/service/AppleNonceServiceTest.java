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
	private RedisTemplate<String, String> redisTemplate;
	@Mock
	private ValueOperations<String, String> valueOperations;

	/** nonce 발급 시 SHA-256 해시값과 5분 TTL이 Redis에 저장되는지 검증한다. */
	@Test
	void issue_일회성_nonce를_Redis에_5분간_저장한다() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		AppleNonceService appleNonceService = new AppleNonceService(redisTemplate);

		String nonce = appleNonceService.issue();

		assertThat(nonce).isNotBlank();
		verify(valueOperations).set(
			eq("APPLE_LOGIN_NONCE:" + AppleNonceService.sha256(nonce)),
			eq("issued"),
			eq(5L),
			eq(TimeUnit.MINUTES)
		);
	}

	/** nonce 원문을 Apple 요청용 SHA-256 해시값으로 변환하는지 검증한다. */
	@Test
	void sha256_nonce를_Apple요청용_해시값으로_변환한다() {
		assertThat(AppleNonceService.sha256("nonce"))
			.isEqualTo("78377b525757b494427f89014f97d79928f3938d14eb51e20fb5dec9834eb304");
	}

	/** 저장된 해시 nonce가 처음 소비될 때만 정상 처리되는지 검증한다. */
	@Test
	void consume_저장된_nonce는_한번만_소비한다() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.getAndDelete("APPLE_LOGIN_NONCE:hashed-nonce")).thenReturn("issued");
		AppleNonceService appleNonceService = new AppleNonceService(redisTemplate);

		boolean consumed = appleNonceService.consume("hashed-nonce");

		assertThat(consumed).isTrue();
	}

	/** 존재하지 않거나 이미 소비된 해시 nonce는 거부하는지 검증한다. */
	@Test
	void consume_없거나_이미_소비한_nonce는_false를_반환한다() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.getAndDelete("APPLE_LOGIN_NONCE:nonce")).thenReturn(null);
		AppleNonceService appleNonceService = new AppleNonceService(redisTemplate);

		boolean consumed = appleNonceService.consume("nonce");

		assertThat(consumed).isFalse();
	}
}
