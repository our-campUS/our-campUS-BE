package com.campus.campus.domain.place.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PartnershipPinResponse(

	@Schema(description = "제휴글 ID", example = "10")
	Long postId,

	@Schema(description = "장소 ID", example = "4")
	Long placeId,

	@Schema(description = "장소 이름", example = "매머드익스프레스 중앙대점")
	String placeName,

	@Schema(description = "장소 위도", example = "37.50775")
	double latitude,

	@Schema(description = "장소 경도", example = "126.96059")
	double longitude,

	@Schema(description = "장소 카테고리", example = "음식점")
	String placeCategory
) {
}
