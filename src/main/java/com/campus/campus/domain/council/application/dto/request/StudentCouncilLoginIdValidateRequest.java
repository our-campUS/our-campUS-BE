package com.campus.campus.domain.council.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record StudentCouncilLoginIdValidateRequest(
	@Schema(description = "회원가입 id", example = "dede1234")
	@NotBlank
	String loginId
) {
}
