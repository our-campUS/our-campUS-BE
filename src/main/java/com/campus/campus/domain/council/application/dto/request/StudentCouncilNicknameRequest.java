package com.campus.campus.domain.council.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record StudentCouncilNicknameRequest(
	@Schema(description = "학생회 닉네임", example = "CUBE")
	@NotBlank
	String councilNickname
) {
}
