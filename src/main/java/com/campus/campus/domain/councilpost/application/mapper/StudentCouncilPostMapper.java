package com.campus.campus.domain.councilpost.application.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.councilpost.application.dto.request.CouncilPostCreatedEvent;
import com.campus.campus.domain.councilpost.application.dto.request.PostRequest;
import com.campus.campus.domain.councilpost.application.dto.response.GetActivePartnershipListForUserResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetLikedPostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostDetailResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostForUserResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostListForCouncilResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetUpcomingEventListForCouncilResponse;
import com.campus.campus.domain.councilpost.application.dto.response.LikePostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.PostListItemResponse;
import com.campus.campus.domain.councilpost.application.dto.response.TodayEventResponse;
import com.campus.campus.domain.councilpost.domain.entity.LikePost;
import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudentCouncilPostMapper {
	private static final ZoneId KST = ZoneId.of("Asia/Seoul");

	public PostListItemResponse toPostListItemResponse(StudentCouncilPost post, boolean isLiked) {
		LocalDateTime now = LocalDateTime.now(KST);

		return new PostListItemResponse(
			post.getId(),
				post.getCategory(),
				post.getTitle(),
				post.getPlace().getPlaceId(),
				post.getPlace().getPlaceName(),
				post.getPlace().getPlaceCategory(),
				post.getDetailedLocation(),
				post.isEvent() ? post.getStartDateTime() : post.getEndDateTime(),
			post.getThumbnailImageUrl(),
			post.getThumbnailIcon(),
			isLiked,
			post.isClosed(now)
		);
	}

	public GetPostListForCouncilResponse toGetPostListForCouncilResponse(StudentCouncilPost post) {
		return new GetPostListForCouncilResponse(
			post.getId(),
			post.getCategory(),
			post.getTitle(),
			post.getPlace().getPlaceName(),
			post.getDetailedLocation(),
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
			post.getPlace().getPlaceName(),
			post.getDetailedLocation(),
			post.getStartDateTime(),
			post.getThumbnailIcon()
		);
	}

	public GetActivePartnershipListForUserResponse toGetActivePartnershipListForUserResponse(StudentCouncilPost post) {
		return new GetActivePartnershipListForUserResponse(
			post.getId(),
			post.getTitle(),
			post.getPlace().getPlaceName(),
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
			.placeName(post.getPlace().getPlaceName())
			.detailedLocation(post.getDetailedLocation())
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

	public GetPostDetailResponse toGetPostDetailResponse(StudentCouncilPost post, List<String> images,
		List<String> placeImageUrls, Long currentCouncilId) {
		var writer = post.getWriter();
		var builder = GetPostDetailResponse.builder()
			.id(post.getId())
			.writerId(writer.getId())
			.writerName(writer.getCouncilName())
			.isWriter(post.isWrittenByCouncil(currentCouncilId))
			.category(post.getCategory())
			.title(post.getTitle())
			.content(post.getContent())
			.place(toSavedPlaceInfo(post.getPlace(), placeImageUrls))
			.detailedLocation(post.getDetailedLocation())
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

	public GetPostForUserResponse toGetPostForUserResponse(StudentCouncilPost post, List<String> images,
		Long currentUserId, boolean isLiked) {
		LocalDateTime now = LocalDateTime.now(KST);
		var writer = post.getWriter();

		var builder = GetPostForUserResponse.builder()
			.id(post.getId())
			.writerId(writer.getId())
			.writerName(writer.getCouncilName())
			.category(post.getCategory())
			.title(post.getTitle())
			.content(post.getContent())
			.place(post.getPlace().getPlaceName())
			.detailedLocation(post.getDetailedLocation())
			.thumbnailImageUrl(post.getThumbnailImageUrl())
			.thumbnailIcon(post.getThumbnailIcon())
			.isLiked(isLiked)
			.isEnded(post.isClosed(now))
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
			post.getPlace().getPlaceName(),
			post.getDetailedLocation(),
			post.isEvent() ? post.getStartDateTime() : post.getEndDateTime(),
			post.getThumbnailImageUrl()
		);
	}

	public TodayEventResponse toTodayRandomEventResponse(StudentCouncilPost post) {
		return new TodayEventResponse(
			post.getId(),
			post.getTitle(),
			post.getContent(),
			post.getPlace() != null ? post.getPlace().getPlaceName() : null,
			post.getStartDateTime()
		);
	}

	public StudentCouncilPost createStudentCouncilPost(StudentCouncil writer, Place place, PostRequest dto,
		LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return StudentCouncilPost.builder()
			.writer(writer)
			.category(dto.category())
			.title(dto.title())
			.content(dto.content())
			.place(place)
			.detailedLocation(dto.detailedLocation())
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

	public CouncilPostCreatedEvent createPostCreatedEvent(StudentCouncilPost post, StudentCouncil writer) {
		String topic = writer.getCouncilType().topic(writer);

		return new CouncilPostCreatedEvent(
			post.getId(),
			writer.getCouncilName(),
			writer,
			post.getCategory(),
			topic
		);
	}

	private SavedPlaceInfo toSavedPlaceInfo(Place place, List<String> imageUrls) {
		if (place == null) {
			return null;
		}

		return new SavedPlaceInfo(
			place.getPlaceId(),
			place.getPlaceName(),
			place.getPlaceKey(),
			place.getAddress(),
			place.getPlaceCategory(),
			place.getNaverPlaceUrl(),
			place.getPhone(),
			place.getCoordinate(),
			imageUrls != null ? imageUrls : Collections.emptyList()
		);
	}
}
