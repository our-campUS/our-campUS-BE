package com.campus.campus.domain.place.application.dto.response;

import com.campus.campus.domain.council.domain.entity.CouncilType;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecommendPartnershipPlaceResponse(
	@Schema(description = "장소 ID", example = "10")
	Long placeId,

	@Schema(description = "장소 이름", example = "봉구스밥버거 중앙대후문점")
	String placeName,

	@Schema(description = "제휴한 학생회 이름", example = "가천대학교 총학생회")
	String councilName,

	@Schema(description = "학생회 타입", example = "SCHOOL_COUNCIL")
	CouncilType councilType,

	@Schema(description = "제휴 이름 (게시글 제목)", example = "전 메뉴 10% 할인")
	String partnershipTitle,

	@Schema(description = "장소 주소", example = "서울특별시 동작구 상도1동")
	String address,

	@Schema(description = "리뷰 평균 별점", example = "4.5")
	Double averageStar,

	@Schema(description = "제휴 썸네일 이미지", example = "https://cdn.example.com/image.jpg")
	String imageUrl
) {
}
