package com.campus.campus.global.util.jwt.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenReissueResponse(
	@Schema(description = "새로운 Access Token")
	String accessToken,

	@Schema(description = "새로운 Refresh Token (RTR 적용 시)")
	String refreshToken
) {
}
