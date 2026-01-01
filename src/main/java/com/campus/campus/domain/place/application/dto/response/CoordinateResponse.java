package com.campus.campus.domain.place.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CoordinateResponse(
	@Schema(description = "위도", example = "37.545419")
	double latitude,

	@Schema(description = "경도", example = "126.964649")
	double longitude
) {
}
