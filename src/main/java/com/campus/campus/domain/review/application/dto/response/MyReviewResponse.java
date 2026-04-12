package com.campus.campus.domain.review.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record MyReviewResponse(
	@Schema(description = "리뷰 ID", example = "1")
	Long reviewId,

	@Schema(description = "장소 ID", example = "10")
	Long placeId,

	@Schema(description = "장소 이름", example = "스타벅스 강남점")
	String placeName,

	@Schema(description = "별점", example = "4.5")
	Double star,

	@Schema(description = "장소 고유 키", example = "123456789")
	String placeKey,

	@Schema(description = "장소 위도", example = "37.497942")
	Double latitude,

	@Schema(description = "장소 경도", example = "127.027621")
	Double longitude,

	@Schema(description = "리뷰 내용", example = "커피가 맛있고 분위기가 좋아요.")
	String content,

	@Schema(description = "리뷰 이미지 URL 목록")
	List<String> imageUrls,

	@Schema(description = "작성일자", example = "2026-01-17")
	LocalDateTime createdAt
) {
}
