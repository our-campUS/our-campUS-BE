package com.campus.campus.domain.user.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserFirstProfileResponse(
	@Schema(description = "학교 이름", example = "가천대학교")
	String schoolName,

	@Schema(description = "단과대 이름", example = "IT융합대학")
	String collegeName,

	@Schema(description = "학과 이름", example = "컴퓨터공학과")
	String majorName
) {
}
