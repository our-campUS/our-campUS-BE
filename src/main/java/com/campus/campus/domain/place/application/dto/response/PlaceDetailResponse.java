package com.campus.campus.domain.place.application.dto.response;

import java.util.List;

import com.campus.campus.domain.review.application.dto.response.SimpleReviewResponse;

public record PlaceDetailResponse(
	boolean isPartnership,
	Long placeId,
	String placeKey,
	String name,
	String category,
	String address,
	Double latitude,
	Double longitude,
	boolean isLiked,
	double star,
	double distance,

	//PlaceImg 이미지 받아오기
	List<String> imgUrls,
	List<SimpleReviewResponse> reviews,
	int reviewSize
) implements PlaceDetailView {
}