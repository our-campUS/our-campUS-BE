package com.campus.campus.domain.place.application.dto.response;

import java.time.LocalDateTime;

public record LikedPlaceListResponse(
	Long likedPlaceId,
	String placeName,
	String placeKey,
	Double latitude,
	Double longitude,
	Boolean isLiked,
	LocalDateTime likedAt,
	String category,
	Double averageStar
) {
	public static LikedPlaceListResponse from(
		Long likedPlaceId,
		String placeName,
		String placeKey,
		Double latitude,
		Double longitude,
		LocalDateTime likedAt,
		String category,
		Double averageStar
	) {
		return new LikedPlaceListResponse(
			likedPlaceId,
			placeName,
			placeKey,
			latitude,
			longitude,
			true,
			likedAt,
			category,
			averageStar
		);
	}
}