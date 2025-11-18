package com.campus.campus.global.auth.application.dto;

public record OauthLoginResponse(
	String accessToken,
	String refreshToken,
	String nickname,
	Long userId,
	Long kakaoId,
	String email,
	String profileImage,
	boolean isProfileNotCompleted
) {
}
