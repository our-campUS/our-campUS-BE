package com.campus.campus.domain.place.application.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecommendPlaceByTimeResponse(
	@Schema(description = "추천 타입 (LUNCH: 점심, CAFE: 카페, NONE: 해당 시간 아님)", example = "LUNCH")
	String type,

	@Schema(description = "추천 제휴 게시글 (최대 2개)")
	List<RecommendPartnershipPlaceResponse> partnershipPosts,

	@Schema(description = "추천 주변 장소 (최대 2개)")
	List<RecommendNearByPlaceResponse> nearbyPlaces
) {
}
