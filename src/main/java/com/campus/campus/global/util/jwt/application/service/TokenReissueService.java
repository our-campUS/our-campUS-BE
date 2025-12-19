package com.campus.campus.global.util.jwt.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.util.jwt.application.dto.request.TokenReissueRequest;
import com.campus.campus.global.util.jwt.application.dto.response.TokenReissueResponse;
import com.campus.campus.global.util.jwt.application.mapper.TokenReissueMapper;
import com.campus.campus.global.util.jwt.JwtAuthenticator;
import com.campus.campus.global.util.jwt.JwtProvider;
import com.campus.campus.global.util.jwt.exception.InvalidJwtException;
import com.campus.campus.global.util.jwt.logout.application.RedisTokenService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenReissueService {
	private final JwtProvider jwtProvider;
	private final RedisTokenService redisTokenService;
	private final JwtAuthenticator jwtAuthenticator;
	private final UserRepository userRepository;
	private final StudentCouncilRepository studentCouncilRepository;
	private final TokenReissueMapper tokenReissueMapper;

	@Value("${jwt.refresh.expiration-seconds}")
	private long refreshTokenExpirationSeconds;

	public TokenReissueResponse reissue(TokenReissueRequest tokenReissueRequest) {
		String refreshToken = tokenReissueRequest.refreshToken();

		jwtAuthenticator.verifyRefreshToken(refreshToken);

		Claims claims = jwtAuthenticator.parseRefreshToken(refreshToken).getPayload();
		String role = claims.get("role", String.class);
		String subject = claims.getSubject();

		String storedRefreshToken = redisTokenService.getRefreshToken(role, subject);

		if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
			throw new InvalidJwtException();
		}

		checkUserExists(role, Long.valueOf(subject));

		Long id = Long.valueOf(subject);
		String newAccessToken;
		String newRefreshToken;

		if ("USER".equals(role)) {
			newAccessToken = jwtProvider.createAccessToken(id);
			newRefreshToken = jwtProvider.createRefreshToken(id);
		} else if ("COUNCIL".equals(role)) {
			newAccessToken = jwtProvider.createCouncilAccessToken(id);
			newRefreshToken = jwtProvider.createCouncilRefreshToken(id);
		} else {
			throw new InvalidJwtException();
		}

		redisTokenService.setRefreshToken(role, subject, newRefreshToken, refreshTokenExpirationSeconds);

		return tokenReissueMapper.toOauthLoginResponse(newAccessToken, newRefreshToken);
	}

	private void checkUserExists(String role, Long id) {
		if ("USER".equals(role)) {
			if (!userRepository.existsById(id)) {
				throw new UserNotFoundException();
			}
		} else if ("COUNCIL".equals(role)) {
			if (!studentCouncilRepository.existsById(id)) {
				throw new StudentCouncilNotFoundException();
			}
		}
	}
}
