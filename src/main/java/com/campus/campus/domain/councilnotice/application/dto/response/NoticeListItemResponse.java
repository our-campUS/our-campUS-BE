package com.campus.campus.domain.councilnotice.application.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record NoticeListItemResponse(
	Long id,
	String title,
	boolean isWriter,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
