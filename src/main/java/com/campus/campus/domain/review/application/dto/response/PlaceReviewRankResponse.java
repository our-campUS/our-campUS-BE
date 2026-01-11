package com.campus.campus.domain.review.application.dto.response;

import lombok.Builder;

@Builder
public record PlaceReviewRankResponse(
	Long placeId,
	String placeName,
	String category,
	String partnership,
	String thumbnailUrl
	// double distance
) {
}
