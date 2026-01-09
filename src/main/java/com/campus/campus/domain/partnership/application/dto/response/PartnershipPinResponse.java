package com.campus.campus.domain.partnership.application.dto.response;

public record PartnershipPinResponse(
	Long postId,
	Long placeId,
	String placeName,
	double latitude,
	double longitude
) {
}
