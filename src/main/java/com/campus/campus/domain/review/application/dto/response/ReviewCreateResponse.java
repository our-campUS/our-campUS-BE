package com.campus.campus.domain.review.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReviewCreateResponse(
	@Schema(description = "작성된 리뷰 정보")
	WriteReviewResponse review,

	@Schema(description = "리뷰 생성 결과 (스탬프 획득 여부 등)")
	ReviewCreateResult result,

	@Schema(description = "리뷰 랭킹 정보")
	ReviewRankingResponse ranking
) {
}
