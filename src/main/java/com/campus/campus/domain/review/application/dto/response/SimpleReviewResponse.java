package com.campus.campus.domain.review.application.dto.response;

import lombok.Builder;

@Builder
public record SimpleReviewResponse(
	double star,
	String writerName,
	String content,
	String thumbnailImgUrl
) {
}