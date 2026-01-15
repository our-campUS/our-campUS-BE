package com.campus.campus.domain.council.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record StudentCouncilChangeProfileImageRequest(
	@Schema(description = "학생회 프로필 이미지 url", example = "https://www.example.com.png")
	String councilProfileImageUrl
) {
}
