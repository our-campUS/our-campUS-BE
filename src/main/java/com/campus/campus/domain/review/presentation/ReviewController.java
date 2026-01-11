package com.campus.campus.domain.review.presentation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.review.application.dto.request.ReviewRequest;
import com.campus.campus.domain.review.application.dto.response.CursorPageReviewResponse;
import com.campus.campus.domain.review.application.dto.response.PlaceReviewRankResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewCreateResponse;
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
	public CommonResponse<ReviewCreateResponse> writeReview(
		@Valid @RequestBody ReviewRequest request,
		@CurrentUserId Long userId
	) {
		ReviewCreateResponse response = reviewService.writeReview(request, userId);
		return CommonResponse.success(ReviewResponseCode.REVIEW_SAVE_SUCCESS, response);
	}

	@GetMapping("/{reviewId}")
	@Operation(summary = "리뷰 상세 조회")
	public CommonResponse<ReviewResponse> readReview(@PathVariable Long reviewId) {
		ReviewResponse response = reviewService.readReview(reviewId);
		return CommonResponse.success(ReviewResponseCode.REVIEW_SAVE_SUCCESS, response);
	}

	@DeleteMapping("/{reviewId}")
	@Operation(summary = "리뷰 삭제")
	public CommonResponse<Void> deleteReview(@PathVariable Long reviewId, @CurrentUserId Long userId) {
		reviewService.delete(userId, reviewId);
		return CommonResponse.success(ReviewResponseCode.REVIEW_DELETE_SUCCESS);
	}

	@PatchMapping("/{reviewId}")
	@Operation(summary = "리뷰 수정")
	public CommonResponse<ReviewResponse> updateReview(
		@CurrentUserId Long userId,
		@PathVariable Long reviewId,
		@RequestBody @Valid ReviewRequest request
	) {
		ReviewResponse response = reviewService.update(userId, reviewId, request);
		return CommonResponse.success(ReviewResponseCode.REVIEW_UPDATE_SUCCESS, response);
	}

	@GetMapping("/list/{placeId}")
	@Operation(summary = "리뷰 목록 조회 (더보기 이후)")
	public CommonResponse<CursorPageReviewResponse<ReviewResponse>> readAllReviews(
		@PathVariable Long placeId,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Long cursorId,
		@RequestParam(defaultValue = "10") int size
	) {
		CursorPageReviewResponse<ReviewResponse> response = reviewService.getReviewList(placeId, cursorCreatedAt,
			cursorId, size);
		return CommonResponse.success(ReviewResponseCode.GET_REVIEW_LIST_SUCCESS, response);

	}

	@GetMapping("/partnership-list")
	@Operation(summary = "제휴 매장 둘러보기", description = "최근 한달 간 제휴 이용수가 많았던 매장")
	public CommonResponse<List<PlaceReviewRankResponse>> readAllPartnerships(
		@CurrentUserId Long userId
	) {
		List<PlaceReviewRankResponse> response = reviewService.readPopularPartnerships(userId);
		return CommonResponse.success(ReviewResponseCode.GET_RANK_SUCCESS, response);
	}
}
