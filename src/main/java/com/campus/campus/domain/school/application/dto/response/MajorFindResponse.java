package com.campus.campus.domain.school.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MajorFindResponse(
	@Schema(description = "단과대 id", example = "10")
	Long collegeId,

	@Schema(description = "단과대 이름", example = "바이오나노대학")
	String collegeName,

	@Schema(description = "학과 id", example = "30")
	Long majorId,

	@Schema(description = "학과 이름", example = "컴퓨터공학전공(컴퓨터공학부)")
	String majorName
) {
}
