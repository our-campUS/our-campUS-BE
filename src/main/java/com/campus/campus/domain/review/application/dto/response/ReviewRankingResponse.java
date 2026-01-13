package com.campus.campus.domain.review.application.dto.response;

public record ReviewRankingResponse(
	RankingScope major,
	RankingScope college,
	RankingScope school
) {
}
