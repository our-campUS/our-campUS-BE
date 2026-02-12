package com.campus.campus.domain.manager.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RewardGrantedEvent(
	@Schema(description = "유저 id", example = "1")
	Long userId,

	@Schema(description = "보상 이름", example = "스타벅스 쿠폰")
	String rewardName
) {
}
