package com.campus.campus.domain.review.application.dto.response;

import java.time.LocalDate;

public record ReviewPartnerResponse(
	String placeName,
	String placeCategory,
	String council,
	double star,
	String title,
	String tag,
	boolean isLiked,
	LocalDate paymentDate
) {
}
