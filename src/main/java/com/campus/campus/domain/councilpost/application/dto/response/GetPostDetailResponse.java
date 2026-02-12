package com.campus.campus.domain.councilpost.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.ThumbnailIcon;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetPostDetailResponse(
	@Schema(description = "게시글 id", example = "1")
	Long id,

	@Schema(description = "작성자 id", example = "1")
	Long writerId,

	@Schema(description = "작성자 이름", example = "가천대학교 총학생회")
	String writerName,

	@Schema(description = "작성자 여부", example = "true")
	Boolean isWriter,

	@Schema(description = "게시글 카테고리", example = "EVENT")
	PostCategory category,

	@Schema(description = "게시글 이름", example = "중간고사 간식생사")
	String title,

	@Schema(description = "게시글 내용", example = "중간고사 간식행사 진행합니다.")
	String content,

	@Schema(description = "장소 정보")
	SavedPlaceInfo place,

	@Schema(description = "상세 장소 (예: 310관 B301호)", example = "310관 B301호")
	String detailedLocation,

	@Schema(description = "시작 일자", example = "2025-04-10")
	LocalDate startDate,

	@Schema(description = "종료 일자", example = "2025-04-30")
	LocalDate endDate,

	@Schema(description = "시작 시간", example = "2025-04-10T18:00")
	LocalDateTime startDateTime,

	@Schema(description = "썸네일 image url", example = "https://www.example.com.png")
	String thumbnailImageUrl,

	@Schema(description = "썸네일 아이콘", example = "FOOD")
	ThumbnailIcon thumbnailIcon,

	@Schema(description = "게시글 image urls")
	List<String> images
) {
}
