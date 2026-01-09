package com.campus.campus.domain.councilpost.application.dto.response;

import java.time.LocalDateTime;

import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.ThumbnailIcon;

public record PostListItemResponse(
	Long id,
	PostCategory category,
	String title,
	String place,
	LocalDateTime endDateTime,
	String thumbnailImageUrl,
	ThumbnailIcon thumbnailIcon,
	Boolean isWriter,
	boolean liked
) {
}
