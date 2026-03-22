package com.campus.campus.domain.place.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
	String partnershipTitle
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
		String partnershipTitle
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
			partnershipTitle
		);
	}
}
