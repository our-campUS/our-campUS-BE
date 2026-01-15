package com.campus.campus.domain.manager.application.dto.request;

import lombok.Builder;

@Builder
public record RewardGrantedEvent(
	Long userId,
	String rewardName
) {
}
