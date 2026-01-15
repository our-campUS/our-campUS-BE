package com.campus.campus.domain.council.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record StudentCouncilNicknameResponse(
	@Schema(description = "학생회 닉네임", example = "CUBE")
	String councilNickname,

	@Schema(description = "학생회 이름", example = "가천대학교 총학생회")
	String councilName
) {
}
