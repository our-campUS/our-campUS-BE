package com.campus.campus.domain.place.application.dto.response.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GooglePhoto(
	@JsonProperty("photo_reference") String photoReference
) {
}