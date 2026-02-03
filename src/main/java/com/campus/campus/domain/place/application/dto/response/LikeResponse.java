package com.campus.campus.domain.place.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LikeResponse(
	@Schema(description = "장소 ID", example = "1")
	Long placeId,

	@Schema(description = "찜 여부 (true: 찜 설정, false: 찜 해제)", example = "true")
	boolean liked
) {
}
