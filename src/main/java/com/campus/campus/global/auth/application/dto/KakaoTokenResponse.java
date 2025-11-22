package com.campus.campus.global.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenResponse(
	@JsonProperty("token_type") String tokenType,
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("access_token_expires_in") Long accessTokenExpiresIn,
	@JsonProperty("refresh_token") String refreshToken,
	@JsonProperty("refresh_token_expires_in") Long refreshTokenExpiresIn,
	String scope
) {
}
