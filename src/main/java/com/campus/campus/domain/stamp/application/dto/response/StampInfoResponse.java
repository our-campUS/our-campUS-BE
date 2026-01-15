package com.campus.campus.domain.stamp.application.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record StampInfoResponse(
	@Schema(description = "스탬프 개수", example = "3")
	int stampCount,

	@Schema(description = "스탬프에 해당하는 리뷰 목록")
	List<StampReviewResponse> reviews
) {
}
