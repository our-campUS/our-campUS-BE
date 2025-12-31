package com.campus.campus.domain.councilNotice.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record NoticeResponseDto(
	Long id,
	Long writerId,
	String writerName,
	boolean isWriter,
	String title,
	String content,
	List<String> images,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
