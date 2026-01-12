package com.campus.campus.domain.review.application.dto.response;

import lombok.Builder;

@Builder
public record ReviewCreateResponse(
	WriteReviewResponse review,
	ReviewCreateResult result,
	ReviewRankingResponse ranking

) {
}
