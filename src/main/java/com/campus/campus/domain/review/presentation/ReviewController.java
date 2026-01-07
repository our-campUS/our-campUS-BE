package com.campus.campus.domain.review.presentation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.review.application.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	// @PostMapping(consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	// @Operation(summary="리뷰 작성")
	// public CommonResponse<ReviewResponse> writeReview(
	//
	// )
}
