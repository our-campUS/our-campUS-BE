package com.campus.campus.domain.user.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.user.application.dto.request.TestLoginRequest;
import com.campus.campus.domain.user.application.service.TestLoginService;
import com.campus.campus.global.auth.application.dto.OauthLoginResponse;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Tag(name = "테스트 로그인")
public class TestLoginController {

	private final TestLoginService testLoginService;

	@PostMapping("/login/review-login-c1a2mp93u")
	@Operation(summary = "리뷰어 테스트 로그인")
	public CommonResponse<OauthLoginResponse> reviewLogin(@RequestBody @Valid TestLoginRequest request) {
		OauthLoginResponse response = testLoginService.login(request.email());
		return CommonResponse.success(UserResponseCode.LOGIN_SUCCESS, response);
	}
}
