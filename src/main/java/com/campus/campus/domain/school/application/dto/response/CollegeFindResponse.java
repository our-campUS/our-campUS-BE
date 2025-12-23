package com.campus.campus.domain.school.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CollegeFindResponse(
	@Schema(description = "단과대 id", example = "10")
	Long collegeId,

	@Schema(description = "단과대 이름", example = "바이오나노대학")
	String collegeName
) {
}
