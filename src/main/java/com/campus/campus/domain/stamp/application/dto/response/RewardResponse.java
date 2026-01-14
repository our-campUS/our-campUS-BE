package com.campus.campus.domain.stamp.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record RewardResponse(
	@Schema(description = "보상 id", example = "1")
	Long rewardId,

	@Schema(description = "보상 이미지 url", example = "https://www.example.com.png")
	String rewardImageUrl,

	@Schema(description = "지급일")
	LocalDateTime rewardDate
) {
}
