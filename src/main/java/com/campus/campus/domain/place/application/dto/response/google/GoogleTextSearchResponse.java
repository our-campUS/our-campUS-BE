package com.campus.campus.domain.place.application.dto.response.google;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTextSearchResponse(
	List<Result> results
) {
	public record Result(
		@JsonProperty("place_id") String placeId
	) {
	}
}


