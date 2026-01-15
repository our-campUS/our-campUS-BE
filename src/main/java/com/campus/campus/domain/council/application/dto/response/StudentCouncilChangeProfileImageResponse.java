package com.campus.campus.domain.council.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record StudentCouncilChangeProfileImageResponse(
	@Schema(description = "학생회 ID", example = "1")
	Long councilId,

	@Schema(description = "학생회 이름", example = "가천대학교 총학생회")
	String councilName,

	@Schema(description = "학생회 프로필 이미지 url", example = "https://www.example.com.png")
	String councilProfileImageUrl
) {
}
