package com.campus.campus.domain.user.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.auth.application.dto.OauthLoginResponse;
import com.campus.campus.global.auth.application.mapper.LoginMapper;
import com.campus.campus.global.util.jwt.JwtProvider;
import com.campus.campus.global.util.jwt.application.service.RedisTokenService;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestLoginService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final RedisTokenService redisTokenService;
	private final LoginMapper loginMapper;

	@Value("${jwt.refresh.expiration-seconds}")
	private long refreshTokenExpirationSeconds;

	@Value("${review.login.enabled:false}")
	private boolean reviewLoginEnabled;

	@Value("${review.login.email}")
	private String reviewUserEmail;

	@Transactional
	public OauthLoginResponse login(String userEmail) {

		// 테스트 로그인 기능 활성화 여부 확인
		if (!reviewLoginEnabled) {
			throw new IllegalStateException("테스트 로그인이 비활성화되어 있습니다.");
		}

		String normalizedInputEmail = userEmail.trim().toLowerCase();
		String normalizedReviewEmail = reviewUserEmail.trim().toLowerCase();

		if (!normalizedReviewEmail.equals(normalizedInputEmail)) {
			throw new UserNotFoundException();
		}

		// 이메일을 기준으로 리뷰 계정 조회
		User user = userRepository
			.findByEmailAndDeletedAtIsNull(normalizedInputEmail)
			.orElseThrow(UserNotFoundException::new);

		String accessToken = jwtProvider.createAccessToken(user.getId());
		String refreshToken = jwtProvider.createRefreshToken(user.getId());

		redisTokenService.setRefreshToken(
			"USER",
			String.valueOf(user.getId()),
			refreshToken,
			refreshTokenExpirationSeconds
		);
		log.info("테스트 계정 로그인 성공: userId={}", user.getId());

		return loginMapper.toOauthLoginResponse(
			user,
			accessToken,
			refreshToken
		);
	}
}
