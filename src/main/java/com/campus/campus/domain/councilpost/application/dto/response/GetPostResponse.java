package com.campus.campus.domain.councilpost.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.ThumbnailIcon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetPostResponse(

	Long id,
	Long writerId,
	String writerName,
	Boolean isWriter,

	PostCategory category,
	String title,
	String content,
	String placeName,
	LocalDate startDate,
	LocalDate endDate,
	LocalDateTime startDateTime,

	String thumbnailImageUrl,
	ThumbnailIcon thumbnailIcon,

	List<String> images
) {
}
