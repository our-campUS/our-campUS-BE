package com.campus.campus.global.util.jwt.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TokenReissueRequest(
	@Schema(description = "refreshToken", example = "현재 유저의 refreshToken")
	@NotBlank
	String refreshToken
) {
}
