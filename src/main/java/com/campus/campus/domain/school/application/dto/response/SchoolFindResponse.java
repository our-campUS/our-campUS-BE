package com.campus.campus.domain.school.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SchoolFindResponse(
	@Schema(description = "학교 id", example = "3")
	Long schoolId,

	@Schema(description = "학교 이름", example = "가천대학교")
	String schoolName
) {
}
