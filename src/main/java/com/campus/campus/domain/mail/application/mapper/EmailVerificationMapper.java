package com.campus.campus.domain.mail.application.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.mail.application.dto.request.EmailVerificationRequest;
import com.campus.campus.domain.mail.domain.entity.EmailVerification;

@Component
public class EmailVerificationMapper {
	public EmailVerification createEmailVerification(String email, String code,
		LocalDateTime expireTime) {
		return EmailVerification.builder()
			.email(email)
			.code(code)
			.expiresAt(expireTime)
			.verified(false)
			.build();
	}
}
