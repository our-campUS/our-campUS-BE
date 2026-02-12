package com.campus.campus.domain.place.application.dto.response;

import java.util.List;

import com.campus.campus.domain.review.application.dto.response.SimpleReviewResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceDetailResponse(
	@Schema(description = "제휴 여부", example = "false")
	boolean isPartnership,

	@Schema(description = "장소 ID", example = "1")
	Long placeId,

	@Schema(description = "장소 고유 키", example = "ChIJN1t_tDeuEmsRUsoyG83frY4")
	String placeKey,

	@Schema(description = "장소 이름", example = "스타벅스 중앙대점")
	String name,

	@Schema(description = "카테고리", example = "CAFE")
	String category,

	@Schema(description = "주소", example = "서울특별시 동작구 흑석로 84")
	String address,

	@Schema(description = "위도", example = "37.5050881")
	Double latitude,

	@Schema(description = "경도", example = "126.9571012")
	Double longitude,

	@Schema(description = "찜 여부", example = "false")
	boolean isLiked,

	@Schema(description = "리뷰 평점", example = "4.5")
	double star,

	@Schema(description = "현재 위치로부터의 거리(m)", example = "150.5")
	double distance,

	//PlaceImg 이미지 받아오기
	@Schema(description = "이미지 URL 목록")
	List<String> imgUrls,

	@Schema(description = "최근 리뷰 목록")
	List<SimpleReviewResponse> reviews,

	@Schema(description = "총 리뷰 개수", example = "10")
	int reviewSize
) implements PlaceDetailView {
}
