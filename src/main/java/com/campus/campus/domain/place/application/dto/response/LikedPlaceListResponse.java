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
	Double averageStar
) {
	public static LikedPlaceListResponse partner(
		Long likedPlaceId,
		String placeName,
		String placeKey,
		Double latitude,
		Double longitude,
		LocalDateTime likedAt,
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
			averageStar
		);
	}

	public static LikedPlaceListResponse nonPartner(
		Long likedPlaceId,
		String placeName,
		String placeKey,
		Double latitude,
		Double longitude,
		LocalDateTime likedAt,
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
			averageStar
		);
	}
}