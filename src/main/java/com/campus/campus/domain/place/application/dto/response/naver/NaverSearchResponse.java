package com.campus.campus.domain.place.application.dto.response.naver;

import java.util.List;

public record NaverSearchResponse(
	List<Item> items
) {

	public record Item(
		String title,
		String category,
		String address,
		String roadAddress,
		String link,
		String telephone,
		String mapx,
		String mapy
	) {
	}
}
