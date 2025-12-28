package com.campus.campus.domain.mail.presentation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.mail.application.dto.request.EmailVerificationConfirmRequest;
import com.campus.campus.domain.mail.application.dto.request.EmailVerificationRequest;
import com.campus.campus.domain.mail.application.service.EmailVerificationService;
import com.campus.campus.domain.mail.domain.entity.VerificationType;
import com.campus.campus.global.annotation.CurrentCouncilId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/council")
public class EmailVerificationController {
	private final EmailVerificationService emailVerificationService;

	@PostMapping("/signup/email/code")
	@Operation(summary = "회원가입 이메일 인증 코드 전송")
	public CommonResponse<Void> sendSignupVerificationCodeEmail(
		@Valid @RequestBody EmailVerificationRequest emailVerificationRequest
	) {
		emailVerificationService.sendSignUpVerificationCode(emailVerificationRequest.email());

		return CommonResponse.success(EmailVerificationResponseCode.EMAIL_SEND_SUCCESS);
	}

	@PostMapping("/signup/email/code/verify")
	@Operation(summary = "회원가입 인증 코드 검증")
	public CommonResponse<Void> verifySignupVerificationCode(
		@Valid @RequestBody EmailVerificationConfirmRequest emailVerificationConfirmRequest
	) {
		emailVerificationService.verifyCode(emailVerificationConfirmRequest, VerificationType.SIGNUP);

		return CommonResponse.success(EmailVerificationResponseCode.VERIFY_SUCCESS);
	}

	@PostMapping("/find/id/email/code")
	@Operation(summary = "학생대표자 아이디 찾기 이메일 인증 코드 전송")
	public CommonResponse<Void> sendFindIdVerificationEmail(
		@Valid @RequestBody EmailVerificationRequest emailVerificationRequest
	) {
		emailVerificationService.sendFindIdVerificationCode(emailVerificationRequest.email());

		return CommonResponse.success(EmailVerificationResponseCode.EMAIL_SEND_SUCCESS);
	}

	@PostMapping("/find/id/email/code/verify")
	@Operation(summary = "학생대표자 아이디 찾기 인증 코드 검증")
	public CommonResponse<Void> verifyFindIdVerificationCode(
		@Valid @RequestBody EmailVerificationConfirmRequest emailVerificationConfirmRequest
	) {
		emailVerificationService.verifyCode(emailVerificationConfirmRequest, VerificationType.FIND_ID);

		return CommonResponse.success(EmailVerificationResponseCode.VERIFY_SUCCESS);
	}

	@PostMapping("/find/password/email/code")
	@Operation(summary = "학생대표자 비밀번호 찾기 이메일 인증 코드 전송")
	public CommonResponse<Void> sendFindPasswordVerificationEmail(
		@Valid @RequestBody EmailVerificationRequest emailVerificationRequest
	) {
		emailVerificationService.sendFindPasswordVerificationCode(emailVerificationRequest.email());

		return CommonResponse.success(EmailVerificationResponseCode.EMAIL_SEND_SUCCESS);
	}

	@PostMapping("/find/password/email/code/verify")
	@Operation(summary = "학생대표자 비밀번호 찾기 인증 코드 검증")
	public CommonResponse<Void> verifyFindPasswordVerificationCode(
		@Valid @RequestBody EmailVerificationConfirmRequest emailVerificationConfirmRequest
	) {
		emailVerificationService.verifyCode(emailVerificationConfirmRequest, VerificationType.FIND_PASSWORD);

		return CommonResponse.success(EmailVerificationResponseCode.VERIFY_SUCCESS);
	}

	@PreAuthorize("hasRole('COUNCIL')")
	@PostMapping("/change/email/code")
	@Operation(summary = "학생회 이메일 변경 인증 코드 전송")
	public CommonResponse<Void> sendChangeEmailVerificationCodeEmail(
		@CurrentCouncilId Long councilId,
		@Valid @RequestBody EmailVerificationRequest emailVerificationRequest
	) {
		emailVerificationService.sendChangeEmailVerificationCode(councilId, emailVerificationRequest.email());

		return CommonResponse.success(EmailVerificationResponseCode.EMAIL_SEND_SUCCESS);
	}

	@PreAuthorize("hasRole('COUNCIL')")
	@PostMapping("/change/email/code/verify")
	@Operation(summary = "학생회 이메일 변경 코드 검증")
	public CommonResponse<Void> verifyChangeEmailVerificationCode(
		@CurrentCouncilId Long councilId,
		@Valid @RequestBody EmailVerificationConfirmRequest emailVerificationConfirmRequest
	) {
		emailVerificationService.verifyChangeEmailCode(councilId, emailVerificationConfirmRequest);

		return CommonResponse.success(EmailVerificationResponseCode.VERIFY_SUCCESS);
	}

}
