package com.campus.campus.domain.user.presentation;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.user.application.dto.request.UserWithdrawRequest;
import com.campus.campus.domain.user.application.service.KakaoOauthService;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.auth.application.dto.OauthLoginResponse;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final KakaoOauthService kakaoOauthService;

	@PostMapping("/login/kakao")
	@Operation(summary = "카카오 로그인 (Native App 방식)")
	public CommonResponse<OauthLoginResponse> kakaoLogin(@RequestParam("token") String kakaoAccessToken) {
		OauthLoginResponse response = kakaoOauthService.login(kakaoAccessToken);

		return CommonResponse.success(UserResponseCode.LOGIN_SUCCESS, response);
	}

	@PatchMapping("/withdraw/users")
	@Operation(summary = "카카오 유저 회원탈퇴")
	public CommonResponse<Void> withdraw(@CurrentUserId Long userId,
		@RequestBody @Valid UserWithdrawRequest userWithdrawRequest) {
		kakaoOauthService.withdraw(userId, userWithdrawRequest.nickname());

		return CommonResponse.success(UserResponseCode.WITHDRAW_SUCCESS);
	}
}