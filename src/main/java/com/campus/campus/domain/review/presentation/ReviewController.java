package com.campus.campus.domain.review.presentation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.campus.campus.domain.review.application.dto.request.ReviewRequest;
import com.campus.campus.domain.review.application.dto.response.CursorPageReviewResponse;
import com.campus.campus.domain.review.application.dto.response.PlaceReviewRankResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewCreateResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewPartnerResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewResponse;
import com.campus.campus.domain.review.application.dto.response.WriteReviewResponse;
import com.campus.campus.domain.review.application.service.OcrService;
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
	private final OcrService ocrService;

	@PostMapping
	@Operation(
		summary = "리뷰 작성",
		description = "제휴 가게여서 영수증 인증을 마쳤다면 isVerified=True값으로 넘겨주세요.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @io.swagger.v3.oas.annotations.media.Content(
				mediaType = "application/json",
				examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
					name = "리뷰 작성 요청 예시",
					summary = "리뷰 작성 Request Body",
					value = """
						{
						  "content": "아주 정말 맛있습니다. 저의 완전 짱 또간집. 꼭꼮꼬꼬꼭 가세요.",
						  "star": 3.5,
						  "imageUrls": [
						    "https://image1.jpg",
						    "https://image2.jpg"
						  ],
						  "place": {
						    "placeName": "숙명여자대학교",
						    "placeKey": "string",
						    "address": "서울특별시 용산구 청파로47길 99",
						    "category": "교육,학문>대학교",
						    "link": "https://map.naver.com/v5/search/%EC%88%99%EB%AA%85%EC%97%AC%EC%9E%90%EB%8C%80%ED%95%99%EA%B5%90",
						    "telephone": "010-1234-1234",
						    "coordinate": {
						      "latitude": 37.545947,
						      "longitude": 126.964578
						    },
						    "imgUrls": [
						      "https://place-image1.jpg"
						    ]
						  }
						}
						"""
				)
			)
		)
	)
	public CommonResponse<ReviewCreateResponse> writeReview(
		@Valid @RequestBody ReviewRequest request,
		@CurrentUserId Long userId
	) {
		ReviewCreateResponse response = reviewService.writeReview(request, userId);
		return CommonResponse.success(ReviewResponseCode.REVIEW_SAVE_SUCCESS, response);
	}

	@PostMapping(
		value = "/receipt-ocr",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	@Operation(
		summary = "영수증 OCR을 통한 제휴 매장 이용 인증",
		description = """
			제휴 매장 리뷰 작성 전, 영수증 OCR을 통해 이용 여부를 인증하는 API입니다.
			
			- 제휴 매장 리뷰 작성 시 반드시 먼저 호출해야 합니다.
			- OCR 인증이 성공적으로 완료된 후 리뷰 작성 API를 호출해주세요.
			- 리뷰 작성 시 isVerified = true 값을 함께 전달해야 합니다.
			- 제휴 매장이 아닌 경우에는 본 API를 호출하지 않고,
			  리뷰 작성 API를 바로 호출하시면 됩니다.
			"""
	)
	public CommonResponse<ReviewPartnerResponse> upload(
		@RequestPart("file") MultipartFile file,
		@RequestParam("placeId") Long placeId,
		@CurrentUserId Long userId
	) {
		return CommonResponse.success(ReviewResponseCode.OCR_SUCCESS, ocrService.processReceipt(file, userId, placeId));
	}

	@GetMapping("/{reviewId}")
	@Operation(summary = "리뷰 상세 조회")
	public CommonResponse<ReviewResponse> readReview(@PathVariable Long reviewId) {
		ReviewResponse response = reviewService.readReview(reviewId);
		return CommonResponse.success(ReviewResponseCode.GET_REVIEW_SUCCESS, response);
	}

	@DeleteMapping("/{reviewId}")
	@Operation(summary = "리뷰 삭제")
	public CommonResponse<Void> deleteReview(@PathVariable Long reviewId, @CurrentUserId Long userId) {
		reviewService.delete(userId, reviewId);
		return CommonResponse.success(ReviewResponseCode.REVIEW_DELETE_SUCCESS);
	}

	@PatchMapping("/{reviewId}")
	@Operation(summary = "리뷰 수정")
	public CommonResponse<WriteReviewResponse> updateReview(
		@CurrentUserId Long userId,
		@PathVariable Long reviewId,
		@RequestBody @Valid ReviewRequest request
	) {
		WriteReviewResponse response = reviewService.update(userId, reviewId, request);
		return CommonResponse.success(ReviewResponseCode.REVIEW_UPDATE_SUCCESS, response);
	}

	@GetMapping("/list/{placeId}")
	@Operation(summary = "리뷰 목록 조회 - 최신순 (더보기 이후)")
	public CommonResponse<CursorPageReviewResponse<ReviewResponse>> readAllReviews(
		@PathVariable Long placeId,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
		@RequestParam(required = false) Long cursorId,
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
