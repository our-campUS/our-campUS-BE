package com.campus.campus.domain.place.application.dto.response.google;

import java.util.List;

public record GooglePlaceDetailResponse(
	Result result
) {
	public record Result(
		List<GooglePhoto> photos
	) {
	}
}
