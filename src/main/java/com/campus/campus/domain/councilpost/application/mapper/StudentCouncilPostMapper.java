package com.campus.campus.domain.councilpost.application.mapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.councilpost.application.dto.response.PostListItemResponseDto;
import com.campus.campus.domain.councilpost.application.dto.request.PostRequestDto;
import com.campus.campus.domain.councilpost.application.dto.response.PostResponseDto;
import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;

public class StudentCouncilPostMapper {

	public static PostListItemResponseDto toListItem(StudentCouncilPost post, Long currentUserId) {
		return new PostListItemResponseDto(
			post.getId(),
			post.getCategory(),
			post.getTitle(),
			post.getPlace(),
			post.getEndDateTime(),
			post.getThumbnailImageUrl(),
			post.getThumbnailIcon(),
			post.getWriter().getId().equals(currentUserId)
		);
	}

	public static PostResponseDto toDetail(StudentCouncilPost post, List<String> images, Long currentUserId) {
		var writer = post.getWriter();
		var builder = PostResponseDto.builder()
			.id(post.getId())
			.writerId(writer.getId())
			.writerName(writer.getFullCouncilName())
			.isWriter(post.isWrittenBy(currentUserId))
			.category(post.getCategory())
			.title(post.getTitle())
			.content(post.getContent())
			.place(post.getPlace())
			.thumbnailImageUrl(post.getThumbnailImageUrl())
			.thumbnailIcon(post.getThumbnailIcon())
			.images(images != null ? images : Collections.emptyList());

		if (post.isEvent()) {
			builder.startDateTime(post.getStartDateTime());
		} else {
			builder.startDate(post.getDisplayStartDate());
			builder.endDate(post.getDisplayEndDate());
		}

		return builder.build();
	}

	public static StudentCouncilPost toEntity(
		StudentCouncil writer, PostRequestDto dto, LocalDateTime startDateTime, LocalDateTime endDateTime
	) {
		return StudentCouncilPost.builder()
			.writer(writer)
			.category(dto.category())
			.title(dto.title())
			.content(dto.content())
			.place(dto.place())
			.startDateTime(startDateTime)
			.endDateTime(endDateTime)
			.thumbnailImageUrl(dto.thumbnailImageUrl())
			.thumbnailIcon(dto.thumbnailIcon())
			.build();
	}

	public static PostImage toEntity(StudentCouncilPost post, String imageUrl) {
		return PostImage.builder()
			.post(post)
			.imageUrl(imageUrl)
			.build();
	}
}
