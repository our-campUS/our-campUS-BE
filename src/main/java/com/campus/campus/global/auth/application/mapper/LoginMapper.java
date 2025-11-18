package com.campus.campus.global.auth.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.global.auth.application.dto.OauthLoginResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginMapper {
	public OauthLoginResponse toOauthLoginResponse(User user, String accessToken, String refreshToken) {
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
