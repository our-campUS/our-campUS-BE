package com.campus.campus.domain.place.application.dto.response.partnership;

import java.util.List;

public record PartnershipScrollResponse(
	List<PartnershipResponse> items,
	boolean hasNext,
	Long nextCursor
) {
}
