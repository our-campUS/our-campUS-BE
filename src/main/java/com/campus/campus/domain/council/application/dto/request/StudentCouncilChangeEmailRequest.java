package com.campus.campus.domain.council.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record StudentCouncilChangeEmailRequest(
	@Schema(description = "변경할 이메일", example = "campus@campus.ac.kr")
	@NotBlank
	String email,

	@Schema(description = "당선 사진 url", example = "https://www.election.com.png")
	@NotBlank
	String electionImageUrl
) {
}
