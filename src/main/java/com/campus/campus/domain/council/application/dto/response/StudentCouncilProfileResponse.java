package com.campus.campus.domain.council.application.dto.response;

public record StudentCouncilProfileResponse(
	Long councilId,
	String councilNickname,
	String councilProfileImageUrl
) {}
