package com.campus.campus.domain.review.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewRankingResponse(
	@Schema(description = "학과 내 랭킹 정보")
	RankingScope major,

	@Schema(description = "단과대 내 랭킹 정보")
	RankingScope college,

	@Schema(description = "학교 전체 랭킹 정보")
	RankingScope school
) {
}
