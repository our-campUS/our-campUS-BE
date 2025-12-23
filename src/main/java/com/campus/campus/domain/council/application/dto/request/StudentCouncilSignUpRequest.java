package com.campus.campus.domain.council.application.dto.request;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.global.annotation.ValidPassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StudentCouncilSignUpRequest(
	@Schema(description = "회원가입 id", example = "dede1234")
	@NotBlank
	String loginId,

	@Schema(description = "비밀번호", example = "qwerqwer")
	@NotBlank
	@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
	@ValidPassword
	String password,

	@Schema(description = "인증 이메일", example = "campus@campus.com")
	@NotBlank
	String email,

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
