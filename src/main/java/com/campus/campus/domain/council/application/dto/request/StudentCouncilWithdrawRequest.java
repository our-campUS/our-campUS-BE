package com.campus.campus.domain.council.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record StudentCouncilWithdrawRequest(
	@Schema(description = "유의사항 동의여부", example = "true")
	Boolean precaution,

	@Schema(description = "비밀번호", example = "qwerqwer")
	@NotBlank
	String password
) {
}
