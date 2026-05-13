package com.campus.campus.domain.user.application.dto.request;

import com.campus.campus.global.annotation.ValidNickname;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CampusNicknameUpdateRequest(
	@Schema(description = "서비스 내 닉네임 (한글/영문/숫자, 2~15자)", example = "캠퍼스러버")
	@NotBlank
	@ValidNickname
	String campusNickname
) {
}
