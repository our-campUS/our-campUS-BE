package com.campus.campus.domain.councilnotice.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record NoticeListItemResponse(
	@Schema(description = "공지 id", example = "1")
	Long id,

	@Schema(description = "공지 이름", example = "중간고사")
	String title,

	@Schema(description = "작성자 여부", example = "true")
	boolean isWriter,

	@Schema(description = "공지 생성 시간", example = "2026-01-10T18:00:00")
	LocalDateTime createdAt,

	@Schema(description = "공지 업데이트 시간", example = "2026-01-10T18:00:00")
	LocalDateTime updatedAt
) {
}
