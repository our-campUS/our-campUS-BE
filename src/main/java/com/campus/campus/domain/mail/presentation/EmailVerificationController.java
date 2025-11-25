package com.campus.campus.domain.mail.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.mail.application.dto.request.EmailVerificationConfirmRequest;
import com.campus.campus.domain.mail.application.dto.request.EmailVerificationRequest;
import com.campus.campus.domain.mail.application.service.EmailVerificationService;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/council/signup/email")
public class EmailVerificationController {
	private final EmailVerificationService emailVerificationService;

	@PostMapping("/code")
	@Operation(summary = "이메일 인증 코드 전송")
	CommonResponse<Void> sendSignupVerificationCodeEmail(
		@Valid @RequestBody EmailVerificationRequest emailVerificationRequest
	) {
		emailVerificationService.sendVerificationCode(emailVerificationRequest.email());

		return CommonResponse.success(EmailVerificationResponseCode.EMAIL_SEND_SUCCESS);
	}

	@PostMapping("/code/verify")
	@Operation(summary = "인증 코드 검증")
	CommonResponse<Void> verifyVerificationCode(
		@Valid @RequestBody EmailVerificationConfirmRequest emailVerificationConfirmRequest
	) {
		emailVerificationService.verifyCode(emailVerificationConfirmRequest);

		return CommonResponse.success(EmailVerificationResponseCode.VERIFY_SUCCESS);
	}
}
