package com.campus.campus.domain.review.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceStarAvgRow(
	@Schema(description = "장소 ID", example = "1")
	Long placeId,

	@Schema(description = "평균 별점", example = "4.5")
	Double avgStar
) {
}
