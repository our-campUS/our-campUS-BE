package com.campus.campus.global.util.jwt.logout.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.global.common.response.CommonResponse;
import com.campus.campus.global.util.jwt.logout.application.LogoutService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LogoutController {
	private final LogoutService logoutService;

	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public CommonResponse<Void> logout(HttpServletRequest request) {
		logoutService.logout(request);

		return CommonResponse.success(LogoutResponseCode.LOGOUT_SUCCESS);
	}
}
