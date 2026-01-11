package com.campus.campus.domain.review.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.review.application.dto.request.ReviewRequest;
import com.campus.campus.domain.review.application.dto.response.ReviewResponse;
import com.campus.campus.domain.review.application.service.ReviewService;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	@Operation(summary = "리뷰 작성")
	public CommonResponse<ReviewResponse> writeReview(
		@Valid @RequestBody ReviewRequest request,
		@CurrentUserId Long userId
	) {
		ReviewResponse response = reviewService.writeReview(request, userId);
		return CommonResponse.success(ReviewResponseCode.REVIEW_SAVE_SUCCESS, response);
	}
}
