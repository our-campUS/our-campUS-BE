package com.campus.campus.domain.place.application.dto.response.google;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record GooglePhoto(
	@Schema(description = "사진 참조 ID", example = "AZ-05OAH2C...")
	@JsonProperty("photo_reference")
	String photoReference
) {
}
