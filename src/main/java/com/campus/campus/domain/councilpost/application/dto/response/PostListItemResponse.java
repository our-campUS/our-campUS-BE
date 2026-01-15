package com.campus.campus.domain.councilpost.application.dto.response;

import java.time.LocalDateTime;

import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.ThumbnailIcon;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostListItemResponse(
	Long id,
	PostCategory category,
	String title,
	String placeName,
	String detailedLocation,
	LocalDateTime endDateTime,
	String thumbnailImageUrl,
	ThumbnailIcon thumbnailIcon,
	boolean liked,
	boolean isEnded
) {
}
