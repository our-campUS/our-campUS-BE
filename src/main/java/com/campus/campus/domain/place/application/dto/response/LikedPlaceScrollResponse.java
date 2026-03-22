package com.campus.campus.domain.place.application.dto.response;

import java.util.List;

public record LikedPlaceScrollResponse(
	List<LikedPlaceDetailResponse> content,
	Long nextCursor,
	Boolean hasNext
) {
	public static LikedPlaceScrollResponse of(
		List<LikedPlaceDetailResponse> content,
		Long nextCursor,
		Boolean hasNext
	) {
		return new LikedPlaceScrollResponse(content, nextCursor, hasNext);
	}
}