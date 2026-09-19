package com.campus.campus.domain.user.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.campus.campus.domain.user.application.exception.UserSignupForbiddenException;
import com.campus.campus.domain.user.application.mapper.UserMapper;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.auth.application.dto.AppleTokenClaims;
import com.campus.campus.global.auth.application.dto.AppleTokenResponse;
import com.campus.campus.global.auth.application.dto.OauthLoginResponse;
import com.campus.campus.global.auth.application.mapper.LoginMapper;
import com.campus.campus.global.auth.application.service.AppleIdTokenVerifier;
import com.campus.campus.global.auth.application.service.AppleTokenClient;
import com.campus.campus.global.util.jwt.JwtProvider;
import com.campus.campus.global.util.jwt.application.service.RedisTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppleOauthService {

	private static final String USER_ROLE = "USER";

	private final AppleTokenClient appleTokenClient;
	private final AppleIdTokenVerifier appleIdTokenVerifier;
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final RedisTokenService redisTokenService;
	private final LoginMapper loginMapper;
	private final UserMapper userMapper;

	@Value("${jwt.refresh.expiration-seconds}")
	private long refreshTokenExpirationSeconds;

	@Transactional
	public OauthLoginResponse login(String authorizationCode, String nickname) {
		AppleTokenResponse appleToken = appleTokenClient.exchangeAuthorizationCode(authorizationCode);
		AppleTokenClaims appleUser = appleIdTokenVerifier.verify(appleToken.idToken());
		User user = findOrCreateUser(appleUser, nickname, appleToken.refreshToken());

		String accessToken = jwtProvider.createAccessToken(user.getId());
		String refreshToken = jwtProvider.createRefreshToken(user.getId());

		redisTokenService.setRefreshToken(
			USER_ROLE,
			String.valueOf(user.getId()),
			refreshToken,
			refreshTokenExpirationSeconds
		);

		return loginMapper.toOauthLoginResponse(user, accessToken, refreshToken);
	}

	private User findOrCreateUser(
		AppleTokenClaims appleUser,
		String nickname,
		String appleRefreshToken
	) {
		return userRepository.findByAppleIdAndDeletedAtIsNull(appleUser.appleId())
			.map(user -> {
				user.updateAppleRefreshToken(appleRefreshToken);
				return user;
			})
			.orElseGet(() -> createUser(appleUser, nickname, appleRefreshToken));
	}

	private User createUser(
		AppleTokenClaims appleUser,
		String nickname,
		String appleRefreshToken
	) {
		if (userRepository.findByAppleId(appleUser.appleId()).isPresent()) {
			throw new UserSignupForbiddenException();
		}

		String resolvedNickname = StringUtils.hasText(nickname)
			? nickname.trim()
			: "apple_" + appleUser.appleId();

		User newUser = userMapper.createAppleUser(
			appleUser.appleId(),
			resolvedNickname,
			appleUser.email(),
			appleRefreshToken
		);

		return userRepository.save(newUser);
	}
}
