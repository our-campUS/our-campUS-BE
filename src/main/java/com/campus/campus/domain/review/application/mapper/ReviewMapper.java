package com.campus.campus.domain.review.application.mapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.review.application.dto.request.ReviewRequest;
import com.campus.campus.domain.review.application.dto.response.CursorPageReviewResponse;
import com.campus.campus.domain.review.application.dto.response.PlaceReviewRankResponse;
import com.campus.campus.domain.review.application.dto.response.RankingScope;
import com.campus.campus.domain.review.application.dto.response.ReviewCreateResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewCreateResult;
import com.campus.campus.domain.review.application.dto.response.ReviewRankingResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewResponse;
import com.campus.campus.domain.review.application.dto.response.SimpleReviewResponse;
import com.campus.campus.domain.review.application.dto.response.WriteReviewResponse;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptItemDto;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptOcrResponse;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptResultDto;
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
			.isVerified(request.isVerified())
			.place(place)
			.build();
	}

	public ReceiptResultDto toReceiptResultDto(
		String storeName, String totalPrice, LocalDate paymentDate, List<ReceiptItemDto> items
	) {
		return new ReceiptResultDto(
			storeName, totalPrice, paymentDate, items
		);
	}

	public ReceiptItemDto toDto(ReceiptOcrResponse.ReceiptOcrItem item) {
		return new ReceiptItemDto(
			safeText(item.name()),
			extractPriceText(item.price())
		);
	}

	private String safeText(ReceiptOcrResponse.TextField field) {
		return field != null ? field.text() : null;
	}

	private String extractPriceText(ReceiptOcrResponse.PriceInfo priceInfo) {
		if (priceInfo == null) {
			return null;
		}
		return safeText(priceInfo.price());
	}

	public CursorPageReviewResponse<ReviewResponse> toEmptyCursorReviewResponse() {
		return CursorPageReviewResponse.<ReviewResponse>builder()
			.items(List.of())
			.nextCursorCreatedAt(null)
			.nextCursorId(null)
			.hasNext(false)
			.build();
	}

	public SimpleReviewResponse toSimpleReviewResponse(Review review, String imageUrl) {
		return SimpleReviewResponse.builder()
			.star(review.getStar())
			.writerName(review.getUser().getNickname())
			.content(review.getContent())
			.thumbnailImgUrl(imageUrl)
			.build();
	}

	public ReviewImage createReviewImage(Review review, String imageUrl) {
		return ReviewImage.builder()
			.review(review)
			.imageUrl(imageUrl)
			.build();
	}

	public ReviewResponse toReviewResponse(Review review, List<String> imageUrls) {
		return ReviewResponse.builder()
			.id(review.getId())
			.userId(review.getUser().getId())
			.userName(review.getUser().getNickname())
			.createDate(review.getCreatedAt().toLocalDate())
			.placeId(review.getPlace().getPlaceId())
			.content(review.getContent())
			.star(review.getStar())
			.imageUrls(imageUrls != null ? imageUrls : Collections.emptyList())
			.build();
	}

	public WriteReviewResponse toWriteReviewResponse(Review review, String imageUrl) {
		return WriteReviewResponse.builder()
			.id(review.getId())
			.userId(review.getUser().getId())
			.userName(review.getUser().getNickname())
			.createDate(review.getCreatedAt().toLocalDate())
			.placeId(review.getPlace().getPlaceId())
			.content(review.getContent())
			.star(review.getStar())
			.imageUrl(imageUrl)
			.build();
	}

	public CursorPageReviewResponse<ReviewResponse> toCursorReviewResponse(List<ReviewResponse> items, Review last,
		boolean hasNext) {
		return CursorPageReviewResponse.<ReviewResponse>builder()
			.items(items)
			.nextCursorCreatedAt(last.getCreatedAt().toString())
			.nextCursorId(last.getId())
			.hasNext(hasNext)
			.build();
	}

	public ReviewCreateResult toReviewCreateResult(boolean isFirstReviewOfPlace, long userReviewCountOfPlace,
		int numberOfUserStamp) {
		return ReviewCreateResult.builder()
			.isFirstReviewOfPlace(isFirstReviewOfPlace)
			.userReviewCountOfPlace((int)userReviewCountOfPlace)
			.numberOfUserStamp(numberOfUserStamp)
			.build();
	}

	public ReviewRankingResponse toReviewRankingResponse(
		String majorName, long majorRank,
		String collegeName, long collegeRank,
		String schoolName, long schoolRank
	) {
		return new ReviewRankingResponse(
			new RankingScope(majorName, majorRank),
			new RankingScope(collegeName, collegeRank),
			new RankingScope(schoolName, schoolRank)
		);
	}

	public ReviewCreateResponse toReviewCreateResponse(WriteReviewResponse response, ReviewCreateResult createResult,
		ReviewRankingResponse rankingResponse) {
		return ReviewCreateResponse.builder()
			.review(response)
			.result(createResult)
			.ranking(rankingResponse)
			.build();
	}

	public PlaceReviewRankResponse toTopPartnershipResponse(
		StudentCouncilPost post
	) {
		return PlaceReviewRankResponse.builder()
			.placeId(post.getPlace().getPlaceId())
			.placeName(post.getPlace().getPlaceName())
			.category(post.getPlace().getPlaceCategory())
			.partnership(post.getTitle())
			.thumbnailUrl(post.getThumbnailImageUrl())
			// .reviewCount(projection.getReviewCount())
			.build();
	}

}
