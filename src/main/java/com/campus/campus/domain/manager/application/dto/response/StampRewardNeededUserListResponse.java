package com.campus.campus.domain.manager.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record StampRewardNeededUserListResponse(
	@Schema(description = "보상 지급이 필요한 사용자 ID", example = "1")
	Long userId,

	@Schema(description = "보상 지급이 필요한 사용자 이름", example = "한승현")
	String nickname,

	@Schema(description = "사용자의 스탬프 수", example = "10")
	int stampCount,

	@Schema(description = "사용자가 보상이 필요한지 여부", example = "true")
	boolean rewardNeeded
) {
}
