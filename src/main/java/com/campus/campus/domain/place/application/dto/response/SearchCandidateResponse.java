package com.campus.campus.domain.place.application.dto.response;

import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;

public record SearchCandidateResponse(
	NaverSearchResponse.Item item,
	String name,
	String address,
	String placeKey,
	String naverPlaceUrl
) {
}
