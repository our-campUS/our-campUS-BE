package com.campus.campus.domain.stamp.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record StampReviewResponse(
	@Schema(description = "리뷰 id", example = "1")
	Long reviewId,

	@Schema(description = "리뷰 장소 이름", example = "투썸 플레이스")
	String placeName,

	@Schema(description = "리뷰 작성 시간", example = "2024-01-10T18:00:00")
	LocalDateTime reviewCreatedAt
) {
}
