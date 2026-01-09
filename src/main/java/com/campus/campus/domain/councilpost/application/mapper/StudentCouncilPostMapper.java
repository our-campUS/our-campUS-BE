package com.campus.campus.domain.councilpost.application.mapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.councilpost.application.dto.response.GetLikedPostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.LikePostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostListForCouncilResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetUpcomingEventListForCouncilResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetActivePartnershipListForUserResponse;
import com.campus.campus.domain.councilpost.application.dto.response.PostListItemResponse;
import com.campus.campus.domain.councilpost.application.dto.request.PostRequest;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostResponseForUser;
import com.campus.campus.domain.councilpost.domain.entity.LikePost;
import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudentCouncilPostMapper {
	public PostListItemResponse toPostListItemResponse(StudentCouncilPost post, Long councilId, boolean isLiked) {
		return new PostListItemResponse(
			post.getId(),
			post.getCategory(),
			post.getTitle(),
			post.getPlace(),
			post.isEvent() ? post.getStartDateTime() : post.getEndDateTime(),
			post.getThumbnailImageUrl(),
			post.getThumbnailIcon(),
			post.isWrittenByCouncil(councilId),
			isLiked
		);
	}

	public GetPostListForCouncilResponse toGetPostListForCouncilResponse(StudentCouncilPost post) {
		return new GetPostListForCouncilResponse(
			post.getId(),
			post.getCategory(),
			post.getTitle(),
			post.getPlace(),
			post.isEvent() ? post.getStartDateTime() : post.getEndDateTime(),
			post.getThumbnailImageUrl(),
			post.getThumbnailIcon()
		);
	}

	public GetUpcomingEventListForCouncilResponse toGetUpcomingEventListForCouncilResponse(StudentCouncilPost post) {
		return new GetUpcomingEventListForCouncilResponse(
			post.getId(),
			post.getCategory(),
			post.getTitle(),
			post.getPlace(),
			post.getStartDateTime(),
			post.getThumbnailIcon()
		);
	}

	public GetActivePartnershipListForUserResponse toGetActivePartnershipListForUserResponse(StudentCouncilPost post) {
		return new GetActivePartnershipListForUserResponse(
			post.getId(),
			post.getTitle(),
			post.getPlace(),
			post.getThumbnailImageUrl()
		);
	}

	public GetPostResponse toGetPostResponse(StudentCouncilPost post, List<String> images, Long currentCouncilId) {
		var writer = post.getWriter();
		var builder = GetPostResponse.builder()
			.id(post.getId())
			.writerId(writer.getId())
			.writerName(writer.getCouncilName())
			.isWriter(post.isWrittenByCouncil(currentCouncilId))
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

	public GetPostResponseForUser toGetPostResponseForUser(StudentCouncilPost post, List<String> images,
		Long currentUserId, boolean isLiked) {
		var writer = post.getWriter();
		var builder = GetPostResponseForUser.builder()
			.id(post.getId())
			.writerId(writer.getId())
			.writerName(writer.getCouncilName())
			.category(post.getCategory())
			.title(post.getTitle())
			.content(post.getContent())
			.place(post.getPlace())
			.thumbnailImageUrl(post.getThumbnailImageUrl())
			.thumbnailIcon(post.getThumbnailIcon())
			.isLiked(isLiked)
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

	public GetLikedPostResponse toGetLikedPostResponse(StudentCouncilPost post) {
		return new GetLikedPostResponse(
			post.getId(),
			post.getTitle(),
			post.getPlace(),
			post.isEvent() ? post.getStartDateTime() : post.getEndDateTime(),
			post.getThumbnailImageUrl()
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
