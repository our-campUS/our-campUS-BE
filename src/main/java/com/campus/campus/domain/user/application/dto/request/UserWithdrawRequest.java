package com.campus.campus.domain.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserWithdrawRequest(
	@Schema(description = "닉네임", example = "한승현")
	@NotBlank(message = "닉네임을 입력해주세요.")
	String nickname
) {
}
