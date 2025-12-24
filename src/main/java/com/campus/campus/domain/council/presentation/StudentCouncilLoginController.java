package com.campus.campus.domain.council.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.council.application.dto.request.StudentCouncilFindPasswordRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilLoginRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilSignUpRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilWithdrawRequest;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilFindIdResponse;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilLoginResponse;
import com.campus.campus.domain.council.application.service.CouncilLoginService;
import com.campus.campus.global.annotation.CurrentUserId;
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
	public CommonResponse<StudentCouncilLoginResponse> Signup(
		@Valid @RequestBody StudentCouncilSignUpRequest studentCouncilSignUpRequest) {
		StudentCouncilLoginResponse response = councilLoginService.signUp(studentCouncilSignUpRequest);

		return CommonResponse.success(StudentCouncilResponseCode.SIGNUP_SUCCESS, response);
	}

	@PostMapping("/login")
	@Operation(summary = "학생회 로그인")
	public CommonResponse<StudentCouncilLoginResponse> Login(
		@Valid @RequestBody StudentCouncilLoginRequest studentCouncilLoginRequest) {
		StudentCouncilLoginResponse response = councilLoginService.login(studentCouncilLoginRequest);

		return CommonResponse.success(StudentCouncilResponseCode.LOGIN_SUCCESS, response);
	}

	@GetMapping("/find/id")
	@Operation(summary = "학생회 대표자 아이디 찾기")
	public CommonResponse<StudentCouncilFindIdResponse> findId(@Valid @RequestParam String email) {
		StudentCouncilFindIdResponse response = councilLoginService.findId(email);

		return CommonResponse.success(StudentCouncilResponseCode.FIND_ID_SUCCESS, response);
	}

	@PatchMapping("/find/password")
	@Operation(summary = "학생회 대표자 비밀번호 찾기 - 비밀번호 재설정")
	public CommonResponse<Void> findId(
		@Valid @RequestBody StudentCouncilFindPasswordRequest studentCouncilFindPasswordRequest) {
		councilLoginService.findPassword(studentCouncilFindPasswordRequest);

		return CommonResponse.success(StudentCouncilResponseCode.FIND_PASSWORD_SUCCESS);
	}

	@PatchMapping("/withdraw")
	@Operation(summary = "학생회 회원탈퇴 (soft delete 방식)")
	public CommonResponse<Void> withdraw(@CurrentUserId Long councilId,
		@Valid @RequestBody StudentCouncilWithdrawRequest studentCouncilWithdrawRequest) {
		councilLoginService.withdrawCouncil(councilId, studentCouncilWithdrawRequest);

		return CommonResponse.success(StudentCouncilResponseCode.COUNCIL_WITHDRAW_SUCCESS);
	}
}
