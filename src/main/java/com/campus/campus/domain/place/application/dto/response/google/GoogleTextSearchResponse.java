package com.campus.campus.domain.place.application.dto.response.google;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record GoogleTextSearchResponse(
	@Schema(description = "검색 결과 목록")
	List<Result> results
) {
	public record Result(
		@Schema(description = "Google Place ID", example = "ChIJN1t_tDeuEmsRUsoyG83frY4")
		@JsonProperty("place_id")
		String placeId
	) {
	}
}
