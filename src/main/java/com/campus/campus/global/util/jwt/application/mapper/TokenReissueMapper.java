package com.campus.campus.global.util.jwt.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.global.util.jwt.application.dto.response.TokenReissueResponse;

@Component
public class TokenReissueMapper {
	public TokenReissueResponse toTokenReissueResponse(String accessToken, String refreshToken) {
		return new TokenReissueResponse(accessToken, refreshToken);
	}
}
