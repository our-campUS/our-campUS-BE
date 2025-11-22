package com.campus.campus.domain.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserProfileRequest(
	@Schema(description = "소속 학교 ID", example = "1")
	Long schoolId,

	@Schema(description = "소속학과 ID", example = "12")
	Long majorId
) {
}
