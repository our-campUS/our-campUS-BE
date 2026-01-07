package com.campus.campus.domain.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserWithdrawRequest(
	@Schema(description = "카카오 이름", example = "한승현")
	@NotBlank(message = "카카오 이름을 입력해주세요.")
	String kakaoName
) {
}
