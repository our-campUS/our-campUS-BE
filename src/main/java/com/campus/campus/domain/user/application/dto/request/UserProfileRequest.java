package com.campus.campus.domain.user.application.dto.request;

import com.campus.campus.domain.user.domain.entity.School;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserProfileRequest(
	@Schema(description = "소속 학교", example = "가천대학교")
	School school,

	@Schema(description = "소속 단과대", example = "IT 융합대학")
	String college,

	@Schema(description = "소속학과", example = "컴퓨터공학과")
	String major
) {
}
