package com.campus.campus.domain.councilpost.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetLikedPostResponse(
	@Schema(description = "게시글 id", example = "1")
	Long postId,

	@Schema(description = "게시글 이름", example = "투썸 제휴")
	String title,

	@Schema(description = "게시글 장소", example = "투썸 플레이스")
	String place,

	@Schema(description = "시간(끝나는 시간 or 행사날짜)", example = "2026-01-10T18:00:00")
	LocalDateTime dateTime,

	@Schema(description = "썸네일 image url", example = "https://www.example.com.png")
	String thumbnailImageUrl
) {
}
