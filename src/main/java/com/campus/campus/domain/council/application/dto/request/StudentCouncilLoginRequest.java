package com.campus.campus.domain.council.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentCouncilLoginRequest(
	@Schema(description = "회원가입할 이메일(id)", example = "campus@campus.com")
	@NotBlank
	String loginId,

	@Schema(description = "비밀번호", example = "qwerqwer")
	@NotBlank
	@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
	String password
) {
}
