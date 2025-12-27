package com.campus.campus.global.oci.application.dto.request;

import com.campus.campus.global.oci.exception.InvalidImageContentTypeException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PresignedUrlRequestDto(

	@Schema(
		description = "이미지 MIME 타입",
		example = "image/png"
	)
	@NotBlank
	String contentType
) {

	@JsonIgnore
	@Schema(hidden = true)
	public String resolveExtension() {
		return switch (contentType) {
			case "image/png" -> ".png";
			case "image/jpeg", "image/jpg" -> ".jpg";
			default -> throw new InvalidImageContentTypeException(contentType);
		};
	}
}


