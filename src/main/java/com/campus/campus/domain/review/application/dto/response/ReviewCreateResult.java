package com.campus.campus.domain.review.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReviewCreateResult(
	@Schema(description = "해당 장소의 첫 리뷰 여부", example = "true")
	boolean isFirstReviewOfPlace,

	@Schema(description = "사용자가 해당 장소에 작성한 리뷰 수", example = "1")
	int userReviewCountOfPlace,

	@Schema(description = "사용자가 보유한 스탬프 개수", example = "5")
	int numberOfUserStamp
) {
}
