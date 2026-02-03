package com.campus.campus.domain.councilpost.application.dto.response;

import java.time.LocalDateTime;

import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.ThumbnailIcon;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostListItemResponse(
	@Schema(description = "게시글 id", example = "1")
	Long id,

	@Schema(description = "게시글 카테고리", example = "EVENT")
	PostCategory category,

	@Schema(description = "게시글 이름", example = "중간고사 간식생사")
	String title,

	@Schema(description = "장소 이름", example = "스타벅스 중앙대점")
	String placeName,

	@Schema(description = "상세 장소 (예: 310관 B301호)", example = "310관 B301호")
	String detailedLocation,

	@Schema(description = "종료 시간", example = "2025-04-10T18:00")
	LocalDateTime endDateTime,

	@Schema(description = "썸네일 image url", example = "https://www.example.com.png")
	String thumbnailImageUrl,

	@Schema(description = "썸네일 아이콘", example = "FOOD")
	ThumbnailIcon thumbnailIcon,

	@Schema(description = "현재 로그인한 유저의 좋아요 여부", example = "true")
	boolean liked,

	@Schema(description = "제휴/행사 종료 여부", example = "true")
	boolean isEnded
) {
}
