package com.campus.campus.domain.studentCouncilNotice.application.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record NoticeListItemResponseDto(
	Long id,
	String title,
	boolean isWriter,
	LocalDateTime createdAt
) {
}
