package com.campus.campus.domain.review.application.dto.response;

import lombok.Builder;

@Builder
public record ReviewCreateResponse(
	ReviewResponse review,
	ReviewCreateResult result,
	ReviewRankingResponse ranking

) {
}
