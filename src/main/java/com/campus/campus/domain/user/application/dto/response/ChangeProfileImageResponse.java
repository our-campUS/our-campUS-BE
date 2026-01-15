package com.campus.campus.domain.user.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeProfileImageResponse(
	@Schema(description = "유저 id", example = "1")
	Long userId,

	@Schema(description = "유저 닉네임(campusNickname 설정했으면 campusNickname, 아니면, nickname")
	String nickname,

	@Schema(description = "새로운 사용자 프로필 이미지", example = "http://image/img_640x640.jpg")
	String newProfileImage
) {
}