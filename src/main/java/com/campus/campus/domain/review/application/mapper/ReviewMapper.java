package com.campus.campus.domain.review.application.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.review.application.dto.request.ReviewRequest;
import com.campus.campus.domain.review.application.dto.response.ReviewResponse;
import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.review.domain.entity.ReviewImage;
import com.campus.campus.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

	public Review createReview(ReviewRequest request, User user, Place place) {
		return Review.builder()
			.user(user)
			.content(request.content())
			.star(request.star())
			.place(place)
			.build();
	}

	public ReviewImage createReviewImage(Review review, String imageUrl) {
		return ReviewImage.builder()
			.review(review)
			.imageUrl(imageUrl)
			.build();
	}

	public ReviewResponse toReviewResponse(Review review, List<String> imageUrls, User user) {
		ReviewResponse.builder()
			.id(review.getId())
			.userId(user.getId())
			.userName(user.getNickname())
			.createDate(review.getCreatedAt().toLocalDate())
			.placeId(review.getPlace().getPlaceId())
			.content(review.getContent())
			.star(review.getStar())
			.imageUrls(imageUrls != null ? imageUrls : Collections.emptyList())
			.build();
	}
}
