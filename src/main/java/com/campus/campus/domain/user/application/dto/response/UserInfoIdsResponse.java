package com.campus.campus.domain.user.application.dto.response;

public record UserInfoIdsResponse(
	Long userId,
	Long schoolId,
	Long collegeId,
	Long majorId
) {
}
