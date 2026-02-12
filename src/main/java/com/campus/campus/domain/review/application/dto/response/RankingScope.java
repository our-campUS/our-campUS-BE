package com.campus.campus.domain.review.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RankingScope(
	@Schema(description = "랭킹 범위 (SCHOOL, COLLEGE, MAJOR, ALL)", example = "SCHOOL")
	String scope,

	@Schema(description = "해당 범위 내 순위 (n번째 리뷰)", example = "1")
	long rank
) {
}
