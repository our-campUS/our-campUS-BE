package com.campus.campus.domain.councilpost.application.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.ThumbnailIcon;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostRequest(

	@NotNull
	PostCategory category,

	@NotBlank
	String title,

	@NotBlank
	String content,

	String placeName,

	@Schema(example = "2025-04-10T18:00")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime startDateTime,

	@Schema(example = "2025-04-30T23:59")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime endDateTime,

	// 썸네일 (둘 중 하나는 필수)
	String thumbnailImageUrl,
	ThumbnailIcon thumbnailIcon,

	// 본문 이미지들
	List<String> imageUrls
) {
}
