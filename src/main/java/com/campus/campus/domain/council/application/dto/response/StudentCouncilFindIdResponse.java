package com.campus.campus.domain.council.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record StudentCouncilFindIdResponse(
	@Schema(description = "로그인 id", example = "dede1234")
	String loginId
) {
}
