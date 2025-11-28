package com.campus.campus.domain.school.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SchoolFindRequest(
	@Schema(description = "검색어", example = "가천")
	String searchWord
) {
}
