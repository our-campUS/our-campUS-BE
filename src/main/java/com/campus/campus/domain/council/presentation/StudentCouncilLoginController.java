package com.campus.campus.domain.council.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.council.application.dto.request.StudentCouncilLoginRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilSignUpRequest;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilLoginResponse;
import com.campus.campus.domain.council.application.service.CouncilLoginService;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/council")
public class StudentCouncilLoginController {
	private final CouncilLoginService councilLoginService;

	@PostMapping("/signup")
	@Operation(summary = "학생회 회원가입")
	CommonResponse<StudentCouncilLoginResponse> Signup(
		@Valid @RequestBody StudentCouncilSignUpRequest studentCouncilSignUpRequest) {
		StudentCouncilLoginResponse response = councilLoginService.signUp(studentCouncilSignUpRequest);

		return CommonResponse.success(StudentCouncilResponseCode.SIGNUP_SUCCESS, response);
	}

	@PostMapping("/login")
	@Operation(summary = "학생회 로그인")
	CommonResponse<StudentCouncilLoginResponse> Login(
		@Valid @RequestBody StudentCouncilLoginRequest studentCouncilLoginRequest) {
		StudentCouncilLoginResponse response = councilLoginService.login(studentCouncilLoginRequest);

		return CommonResponse.success(StudentCouncilResponseCode.LOGIN_SUCCESS, response);
	}
}
