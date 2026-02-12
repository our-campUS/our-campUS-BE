package com.campus.campus.domain.user.application.dto.response;

import lombok.Builder;

@Builder
public record UserProfileResponse(
	Long userId,
	String campusNickname,
	String profileImage
) {}