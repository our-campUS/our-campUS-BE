package com.campus.campus.domain.review.application.dto.response;

import lombok.Builder;

@Builder
public record RankingScope(
	String scope,
	long rank //n번째 리뷰
) {
}
