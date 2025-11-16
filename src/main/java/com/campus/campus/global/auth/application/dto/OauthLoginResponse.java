package com.campus.campus.global.auth.application.dto;

import com.campus.campus.domain.user.domain.entity.User;

public record OauthLoginResponse(
	String accessToken,
	String refreshToken,
	String nickname,
	Long userId,
	Long kakaoId,
	String email,
	String profileImage
) {
	public static OauthLoginResponse from(User user, String accessToken, String refreshToken) {
		return new OauthLoginResponse(
			accessToken,
			refreshToken,
			user.getNickname(),
			user.getId(),
			user.getKakaoId(),
			user.getEmail(),
			user.getProfileImage()
		);
	}
}
