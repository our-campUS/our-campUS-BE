package com.campus.campus.domain.place.application.dto.response.partnership;

import java.util.List;

public record PartnershipMapResponse(
	Long placeId,
	Double latitude,
	Double longitude,
	List<String> tags
) {
}

