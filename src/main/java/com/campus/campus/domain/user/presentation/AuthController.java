package com.campus.campus.domain.user.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.global.auth.application.dto.OauthLoginResponse;
import com.campus.campus.domain.user.application.service.KakaoOauthService;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login")
public class AuthController {
	private final KakaoOauthService kakaoOauthService;

	@PostMapping("/kakao")
	@Operation(summary = "카카오 로그인")
	public CommonResponse<OauthLoginResponse> kakaoLogin(@RequestParam("code") String code) {
		OauthLoginResponse response = kakaoOauthService.login(code);

		return CommonResponse.success(UserResponseCode.LOGIN_SUCCESS, response);
	}
}
