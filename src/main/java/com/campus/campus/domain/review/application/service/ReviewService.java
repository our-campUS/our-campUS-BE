package com.campus.campus.domain.review.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.place.application.service.PlaceService;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.review.application.dto.request.ReviewRequest;
import com.campus.campus.domain.review.application.dto.response.ReviewResponse;
import com.campus.campus.domain.review.application.mapper.ReviewMapper;
import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.review.domain.entity.ReviewImage;
import com.campus.campus.domain.review.domain.repository.ReviewImageRepository;
import com.campus.campus.domain.review.domain.repository.ReviewRepository;
import com.campus.campus.domain.user.application.exception.UserNotFirstLoginException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

	private final UserRepository userRepository;
	private final ReviewMapper reviewMapper;
	private final ReviewRepository reviewRepository;
	private final PlaceService placeService;
	private final ReviewImageRepository reviewImageRepository;

	@Transactional
	public ReviewResponse writeReview(ReviewRequest request, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFirstLoginException::new);

		//place 객체 생성
		Place place = placeService.findOrCreatePlace(request.place());

		Review review = reviewMapper.createReview(request, user, place);
		reviewRepository.save(review);

		if (request.imageUrls() != null) {
			for (String imageUrl : request.imageUrls()) {
				reviewImageRepository.save(reviewMapper.createReviewImage(review, imageUrl));
			}
		}

		List<String> imageUrls = reviewImageRepository
			.findAllByReviewOrderbyIdAsc(review)
			.stream()
			.map(ReviewImage::getImageUrl)
			.toList();

		return reviewMapper.toReviewResponse(review, imageUrls, user);
	}

}
