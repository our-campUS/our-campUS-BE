package com.campus.campus.domain.place.application.dto.response.partnership;

import com.campus.campus.domain.council.domain.entity.CouncilType;

public record PartnershipPlaceSummary(
	Long placeId,
	String placeKey,
	String name,
	String category,
	String address,
	Double latitude,
	Double longitude,

	// Long councilId,
	CouncilType councilType
) {
}
