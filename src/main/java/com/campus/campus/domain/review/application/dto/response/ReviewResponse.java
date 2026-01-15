package com.campus.campus.domain.review.application.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

@Builder
public record ReviewResponse(
	Long id,
	Long userId,
	String userName,
	LocalDate createDate,
	Long placeId,
	String content,
	double star,
	List<String> imageUrls
) {
}
