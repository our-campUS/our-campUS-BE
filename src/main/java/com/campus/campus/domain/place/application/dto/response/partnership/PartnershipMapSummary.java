package com.campus.campus.domain.place.application.dto.response.partnership;

import com.campus.campus.domain.council.domain.entity.CouncilType;

public record PartnershipMapSummary(
	Long placeId,
	Double latitude,
	Double longitude,
	CouncilType councilType
) {
}

