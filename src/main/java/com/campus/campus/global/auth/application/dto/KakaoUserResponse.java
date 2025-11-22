package com.campus.campus.global.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(
	Long id,
	@JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {

	public record KakaoAccount(
		KakaoProfile profile,
		String email
	) {}

	public record KakaoProfile(
		String nickname,
		@JsonProperty("profile_image_url") String profileImageUrl
	) {}
}
