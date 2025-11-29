package com.campus.campus.domain.mail.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(
	@Schema(description = "인증할 이메일", example = "campus@campus.com")
	@NotBlank
	String email
) {
}
