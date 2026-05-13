package com.campus.campus.domain.place.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.campus.campus.domain.council.domain.entity.CouncilType;

public record LikedPlaceDetailResponse(
	Long likedPlaceId,
	Long placeId,
	String placeName,
	String placeKey,
	Double latitude,
	Double longitude,
	Boolean isLiked,
	LocalDateTime likedAt,
	String category,
	Boolean isPartnership,
	Double distanceMeter,
	Double averageStar,
	List<String> imageUrls,
	String partnershipTitle,
	CouncilType councilType
) {
	public static LikedPlaceDetailResponse of(
		Long likedPlaceId,
		Long placeId,
		String placeName,
		String placeKey,
		Double latitude,
		Double longitude,
		Boolean isLiked,
		LocalDateTime likedAt,
		String category,
		Boolean isPartnership,
		Double distanceMeter,
		Double averageStar,
		List<String> imageUrls,
		String partnershipTitle,
		CouncilType councilType
	) {
		return new LikedPlaceDetailResponse(
			likedPlaceId,
			placeId,
			placeName,
			placeKey,
			latitude,
			longitude,
			isLiked,
			likedAt,
			category,
			isPartnership,
			distanceMeter,
			averageStar,
			imageUrls,
			partnershipTitle,
			councilType
		);
	}
}
