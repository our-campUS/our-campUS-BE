package com.campus.campus.domain.user.presentation;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.auth.application.dto.OauthLoginResponse;
import com.campus.campus.global.auth.application.mapper.LoginMapper;
import com.campus.campus.global.common.response.CommonResponse;
import com.campus.campus.global.util.jwt.JwtProvider;
import com.campus.campus.global.util.jwt.application.service.RedisTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

@Profile({"local", "dev"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
@Tag(name = "Dev 전용", description = "개발 환경 전용 API (local, dev 프로파일에서만 동작)")
public class DevAuthController {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final RedisTokenService redisTokenService;
	private final LoginMapper loginMapper;

	@Value("${jwt.refresh.expiration-seconds}")
	private long refreshTokenExpirationSeconds;

	@PostMapping("/login")
	@Operation(summary = "Dev 전용 유저 로그인", description = "userId만으로 JWT 토큰을 발급합니다. 카카오 OAuth 없이 테스트용으로 사용합니다.")
	public CommonResponse<OauthLoginResponse> devLogin(@RequestParam("userId") Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		String accessToken = jwtProvider.createAccessToken(user.getId());
		String refreshToken = jwtProvider.createRefreshToken(user.getId());

		redisTokenService.setRefreshToken("USER", String.valueOf(user.getId()), refreshToken,
			refreshTokenExpirationSeconds);

		return CommonResponse.success(UserResponseCode.LOGIN_SUCCESS,
			loginMapper.toOauthLoginResponse(user, accessToken, refreshToken));
	}
}
