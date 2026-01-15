package com.campus.campus.domain.councilpost.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record TodayEventResponse(
	@Schema(description = "게시글 id", example = "1")
	Long postId,

	@Schema(description = "게시글 제목", example = "2025 봄 축제")
	String title,

	@Schema(description = "게시글 내용", example = "오늘은 에스파의 공연이 있습니다.")
	String content,

	@Schema(description = "장소 이름", example = "대운동장")
	String placeName,

	@Schema(description = "행사 시작 시간", example = "2026-01-16T18:00:00")
	LocalDateTime startDateTime
) {
}
