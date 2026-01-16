package com.campus.campus.domain.notification.application.dto;

import java.time.LocalDateTime;

public record NextCursor(
	LocalDateTime createdAt,
	Long id
) {
}
