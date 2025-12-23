package com.campus.campus.domain.mail.application.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.mail.application.dto.request.EmailVerificationRequest;
import com.campus.campus.domain.mail.domain.entity.EmailVerification;
import com.campus.campus.domain.mail.domain.entity.VerificationType;

@Component
public class EmailVerificationMapper {
	public EmailVerification createSignupEmailVerification(String email, String code,
		LocalDateTime expireTime) {
		return EmailVerification.builder()
			.email(email)
			.verificationType(VerificationType.SIGNUP)
			.code(code)
			.expiresAt(expireTime)
			.verified(false)
			.build();
	}

	public EmailVerification createFindIdEmailVerification(String email, String code,
		LocalDateTime expireTime) {
		return EmailVerification.builder()
			.email(email)
			.verificationType(VerificationType.FIND_ID)
			.code(code)
			.expiresAt(expireTime)
			.verified(false)
			.build();
	}

	public EmailVerification createFindPasswordEmailVerification(String email, String code,
		LocalDateTime expireTime) {
		return EmailVerification.builder()
			.email(email)
			.verificationType(VerificationType.FIND_PASSWORD)
			.code(code)
			.expiresAt(expireTime)
			.verified(false)
			.build();
	}

	public EmailVerification changeEmailVerification(String email, String code,
		LocalDateTime expireTime) {
		return EmailVerification.builder()
			.email(email)
			.verificationType(VerificationType.CHANGE_EMAIL)
			.code(code)
			.expiresAt(expireTime)
			.verified(false)
			.build();
	}
}
