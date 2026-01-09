package com.campus.campus.domain.council.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ValidateEmailToFindPasswordRequest(
	@Schema(description = "회원가입 id", example = "dede1234")
	@NotBlank
	String loginId,

	@Schema(description = "인증 이메일", example = "campus@campus.com")
	@NotBlank
	String email
) {
}
