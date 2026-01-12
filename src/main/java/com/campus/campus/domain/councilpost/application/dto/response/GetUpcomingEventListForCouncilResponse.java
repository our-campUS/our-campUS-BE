package com.campus.campus.domain.councilpost.application.dto.response;

import java.time.LocalDateTime;

import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.ThumbnailIcon;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetUpcomingEventListForCouncilResponse(
	@Schema(description = "게시글 id", example = "1")
	Long postId,

	@Schema(description = "게시글 카테고리", example = "EVENT")
	PostCategory category,

	@Schema(description = "게시글 이름", example = "중간고사 간식생사")
	String title,

	@Schema(description = "장소", example = "310관 1층")
	String place,

	@Schema(description = "상세 장소", example = "가천관 301호")
	String detailedLocation,

	@Schema(description = "시간(끝나는 시간 or 행사날짜", example = "2026-01-10T18:00:00")
	LocalDateTime dateTime,

	@Schema(description = "썸네일 아이콘", example = "FOOD")
	ThumbnailIcon thumbnailIcon
) {
}
