package com.campus.campus.domain.review.application.dto.response.ocr;

public record TextField(
	String text,
	Formatted formatted,
	Double confidenceScore
) {
	public record Formatted(
		String value
	) {

	}
}
