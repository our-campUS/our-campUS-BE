package com.campus.campus.domain.review.application.dto.response;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record WriteReviewResponse(
	Long id,
	Long userId,
	String userName,
	LocalDate createDate,
	Long placeId,
	String content,
	Double star,
	String imageUrl
) {
}
