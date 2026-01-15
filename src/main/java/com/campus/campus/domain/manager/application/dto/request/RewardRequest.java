package com.campus.campus.domain.manager.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RewardRequest(
	@NotBlank
	@Schema(description = "보상으로 지급 이미지 url", example = "https://www.example.com.png")
	String rewardImageUrl
) {
}
