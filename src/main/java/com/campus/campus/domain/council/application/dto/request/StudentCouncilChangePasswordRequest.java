package com.campus.campus.domain.council.application.dto.request;

import com.campus.campus.global.annotation.ValidPassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentCouncilChangePasswordRequest(
	@Schema(description = "기존 비밀번호", example = "qwerqwer")
	@NotBlank
	String currentPassword,

	@Schema(description = "새 비밀번호", example = "qwerqwerqwer")
	@NotBlank
	@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
	@ValidPassword
	String newPassword,

	@Schema(description = "새 비밀번호 재입력", example = "qwerqwerqwer")
	@NotBlank
	String newPasswordConfirm
) {
}
