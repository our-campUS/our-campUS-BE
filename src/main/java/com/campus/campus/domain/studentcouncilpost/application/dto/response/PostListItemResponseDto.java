package com.campus.campus.domain.studentcouncilpost.application.dto.response;

import java.time.LocalDateTime;

import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.domain.studentcouncilpost.domain.entity.ThumbnailIcon;

public record PostListItemResponseDto(
	Long id,
	PostCategory category,
	String title,
	String place,
	LocalDateTime endDateTime,
	String thumbnailImageUrl,
	ThumbnailIcon thumbnailIcon,
	Boolean isWriter
) {
}