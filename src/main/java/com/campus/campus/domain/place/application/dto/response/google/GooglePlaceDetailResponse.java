package com.campus.campus.domain.place.application.dto.response.google;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record GooglePlaceDetailResponse(
	@Schema(description = "상세 정보 결과")
	Result result
) {
	public record Result(
		@Schema(description = "장소 사진 목록")
		List<GooglePhoto> photos
	) {
	}
}
