package com.campus.campus.domain.councilpost.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record NormalizedDateTime(
	@Schema(description = "시작 시간", example = "2025-04-10T18:00")
	LocalDateTime startDateTime,

	@Schema(description = "종료 시간", example = "2025-04-10T18:00")
	LocalDateTime endDateTime
) {
}
