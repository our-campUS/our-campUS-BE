package com.campus.campus.domain.user.application.dto.response;

import lombok.Builder;

@Builder
public record UserFirstProfileResponse(
	String schoolName,
	String collegeName,
	String majorName
) {
}
