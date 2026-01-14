package com.campus.campus.domain.review.application.dto.response;

import lombok.Builder;

@Builder
public record ReviewCreateResult(
	boolean isFirstReviewOfPlace,
	int userReviewCountOfPlace,
	int numberOfUserStamp
) {
}
