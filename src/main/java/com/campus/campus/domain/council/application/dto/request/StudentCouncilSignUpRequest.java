package com.campus.campus.domain.council.application.dto.request;

import com.campus.campus.domain.council.domain.entity.CouncilType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentCouncilSignUpRequest(
	@Schema(description = "회원가입할 이메일(id)", example = "campus@campus.com")
	@NotBlank
	String loginId,

	@Schema(description = "비밀번호", example = "qwerqwer")
	@NotBlank
	String password,

	@Schema(description = "학생회 종류", example = "SCHOOL_COUNCIL")
	@NotNull
	CouncilType councilType,

	@Schema(description = "학교 id", example = "1")
	@NotNull
	Long schoolId,

	@Schema(description = "단과대 id", example = "1")
	Long collegeId,

	@Schema(description = "학과 id", example = "1")
	Long majorId
) {
}
