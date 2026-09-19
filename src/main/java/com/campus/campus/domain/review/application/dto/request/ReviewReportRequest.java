package com.campus.campus.domain.review.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewReportRequest(
	@NotBlank
	@Size(max = 500)
	String reason
) {
}
