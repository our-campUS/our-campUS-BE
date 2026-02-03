package com.campus.campus.domain.review.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PlaceReviewRankResponse(
	@Schema(description = "장소 ID", example = "1")
	Long placeId,

	@Schema(description = "장소 이름", example = "스타벅스 중앙대점")
	String placeName,

	@Schema(description = "카테고리", example = "CAFE")
	String category,

	@Schema(description = "제휴 정보 (없으면 null)", example = "총학생회 제휴")
	String partnership,

	@Schema(description = "썸네일 이미지 URL", example = "https://example.com/image.jpg")
	String thumbnailUrl,

	@Schema(description = "거리(m)", example = "150.5")
	double distance
) {
}
