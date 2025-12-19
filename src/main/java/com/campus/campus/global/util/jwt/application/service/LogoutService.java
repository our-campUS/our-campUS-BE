package com.campus.campus.global.util.jwt.application.service;

import org.springframework.stereotype.Service;

import com.campus.campus.global.util.jwt.JwtProvider;
import com.campus.campus.global.util.jwt.application.dto.AuthenticationInfo;
import com.campus.campus.global.util.jwt.exception.InvalidJwtException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService {

	private final JwtProvider jwtProvider;
	private final RedisTokenService redisTokenService;

	public void logout(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new InvalidJwtException();
		}

		String accessToken = authHeader.substring(7);

		// 1. Access Token 검증 (유효하지 않은 토큰이면 에러)
		AuthenticationInfo info = jwtProvider.getAuthenticationInfo(accessToken);

		// 2. Access Token 만료 시간 가져오기
		Long expiration = jwtProvider.getAccessTokenExpiration(accessToken);

		// 3. Redis에 Access Token 블랙리스트 등록 (남은 시간만큼)
		// Key: accessToken, Value: "logout"
		if (expiration > 0) {
			redisTokenService.setBlackList(accessToken, "logout", expiration);
		}

		// 4. Redis에서 Refresh Token 삭제
		redisTokenService.deleteRefreshToken(info.role(), String.valueOf(info.id()));
	}
}
