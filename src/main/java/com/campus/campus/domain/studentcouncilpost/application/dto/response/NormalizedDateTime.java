package com.campus.campus.domain.studentcouncilpost.application.dto.response;

import java.time.LocalDateTime;

public record NormalizedDateTime(
	LocalDateTime startDateTime,
	LocalDateTime endDateTime
) {
}
