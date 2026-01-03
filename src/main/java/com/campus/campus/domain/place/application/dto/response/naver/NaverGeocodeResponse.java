package com.campus.campus.domain.place.application.dto.response.naver;

import java.util.List;

@SuppressWarnings({"checkstyle:RegexpMultiline", "checkstyle:WhitespaceAround", "checkstyle:MethodParamPad"})
public record NaverGeocodeResponse(
	List<AddressItem> addresses
) {
	@SuppressWarnings({"checkstyle:RegexpMultiline", "checkstyle:MethodParamPad"})
	public record AddressItem(
		String x, //longitude
		String y //latitude
	) {
	}
}
