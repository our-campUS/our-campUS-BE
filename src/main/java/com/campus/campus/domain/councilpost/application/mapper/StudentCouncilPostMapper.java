package com.campus.campus.domain.councilpost.application.mapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.councilpost.application.dto.response.LikePostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.PostListItemResponse;
import com.campus.campus.domain.councilpost.application.dto.request.PostRequest;
import com.campus.campus.domain.councilpost.application.dto.response.PostResponse;
import com.campus.campus.domain.councilpost.domain.entity.LikePost;
import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudentCouncilPostMapper {

	public PostListItemResponse toPostListItemResponse(StudentCouncilPost post, Long currentUserId) {
		return new PostListItemResponse(
			post.getId(),
			post.getCategory(),
			post.getTitle(),
			post.getPlace(),
			post.isEvent()
				? post.getStartDateTime()
				: post.getEndDateTime(),
			post.getThumbnailImageUrl(),
			post.getThumbnailIcon(),
			post.isWrittenByCouncil(currentUserId)
		);
	}

	public PostResponse toPostResponse(StudentCouncilPost post, List<String> images, Long currentUserId) {
		var writer = post.getWriter();
		var builder = PostResponse.builder()
			.id(post.getId())
			.writerId(writer.getId())
			.writerName(writer.getCouncilName())
			.isWriter(post.isWrittenByCouncil(currentUserId))
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

	public LikePostResponse toLikePostResponse(User user, StudentCouncilPost post, boolean liked) {
		return new LikePostResponse(
			user.getId(),
			post.getId(),
			liked
		);
	}

	public StudentCouncilPost createStudentCouncilPost(StudentCouncil writer, PostRequest dto,
		LocalDateTime startDateTime, LocalDateTime endDateTime) {
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

	public PostImage createPostImage(StudentCouncilPost post, String imageUrl) {
		return PostImage.builder()
			.post(post)
			.imageUrl(imageUrl)
			.build();
	}

	public LikePost createLikePost(User user, StudentCouncilPost post) {
		return LikePost.builder()
			.post(post)
			.user(user)
			.build();
	}
}
