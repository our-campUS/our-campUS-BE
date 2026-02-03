package com.campus.campus.domain.user.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserInfoIdsResponse(
	@Schema(description = "유저 ID", example = "1")
	Long userId,

	@Schema(description = "학교 ID", example = "1")
	Long schoolId,

	@Schema(description = "단과대 ID", example = "1")
	Long collegeId,

	@Schema(description = "학과 ID", example = "10")
	Long majorId
) {
}
