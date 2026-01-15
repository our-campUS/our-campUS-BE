package com.campus.campus.domain.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeProfileImageRequest(
	@Schema(description = "새로운 사용자 프로필 이미지", example = "http://image/img_640x640.jpg")
	String newProfileImage
) {
}
