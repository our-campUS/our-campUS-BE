package com.campus.campus.domain.review.application.dto.response;

public record ReviewPartnerResponse(
	String placeName,
	String placeCategory,
	String council,
	double star,
	String title
) {
}
