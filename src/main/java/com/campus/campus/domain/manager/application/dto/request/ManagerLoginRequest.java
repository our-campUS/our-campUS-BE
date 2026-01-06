package com.campus.campus.domain.manager.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ManagerLoginRequest(
	@NotBlank
	@Schema(description = "로그인 id", example = "illtathebest1")
	String loginId,

	@NotBlank
	@Schema(description = "로그인 비밀번호", example = "illtathebest")
	String password
) {
}
