package com.campus.campus.domain.manager.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.manager.application.dto.request.ManagerLoginRequest;
import com.campus.campus.domain.manager.application.dto.response.ManagerLoginResponse;
import com.campus.campus.domain.manager.application.exception.ManagerNotFoundException;
import com.campus.campus.domain.manager.application.exception.PasswordNotCorrectException;
import com.campus.campus.domain.manager.application.mapper.ManagerMapper;
import com.campus.campus.domain.manager.domain.entity.Manager;
import com.campus.campus.domain.manager.domain.repository.ManagerRepository;
import com.campus.campus.global.util.jwt.JwtProvider;
import com.campus.campus.global.util.jwt.application.service.RedisTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {
	private final ManagerRepository managerRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;
	private final RedisTokenService redisTokenService;
	private final ManagerMapper managerMapper;

	@Value("${jwt.refresh.expiration-seconds}")
	private long refreshTokenExpirationSeconds;

	public ManagerLoginResponse login(ManagerLoginRequest managerLoginRequest) {
		Manager manager = managerRepository.findByLoginId(managerLoginRequest.loginId())
			.orElseThrow(ManagerNotFoundException::new);

		if (!passwordEncoder.matches(managerLoginRequest.password(), manager.getPassword())) {
			throw new PasswordNotCorrectException();
		}

		String accessToken = jwtProvider.createManagerAccessToken(manager.getId());
		String refreshToken = jwtProvider.createManagerRefreshToken(manager.getId());

		redisTokenService.setRefreshToken("MANAGER", String.valueOf(manager.getId()), refreshToken,
			refreshTokenExpirationSeconds);

		return managerMapper.toManagerLoginResponse(manager, accessToken, refreshToken);
	}
}
