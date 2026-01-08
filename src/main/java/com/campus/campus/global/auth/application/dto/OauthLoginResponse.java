package com.campus.campus.global.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record OauthLoginResponse(
	@Schema(description = "인증된 access token", example = "랜덤 accessToken")
	String accessToken,

	@Schema(description = "인증된 refresh token", example = "랜덤 refreshToken")
	String refreshToken,

	@Schema(description = "사용자 이름(닉네임)", example = "김잇타")
	String nickname,

	@Schema(description = "사용자 서비스 내 닉네임", example = "망포동 피바라기")
	String campusNickname,

	@Schema(description = "사용자 id", example = "1")
	Long userId,

	@Schema(description = "사용자 kakaoId", example = "4548160187")
	Long kakaoId,

	@Schema(description = "사용자 이메일", example = "itta@naver.com")
	String email,

	@Schema(description = "사용자 프로필 이미지", example = "http://image/img_640x640.jpg")
	String profileImage,

	@Schema(description = "사용자 프로필(학적 정보) 작성 여부", example = "false")
	boolean isProfileNotCompleted
) {
}
