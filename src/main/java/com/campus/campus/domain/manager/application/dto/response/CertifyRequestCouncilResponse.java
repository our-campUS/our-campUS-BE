package com.campus.campus.domain.manager.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CertifyRequestCouncilResponse(
	@Schema(description = "인증 요청한 학생회 id", example = "1")
	Long councilId,

	@Schema(description = "인증 요청한 학생회 이름", example = "가천대학교 총학생회")
	String councilName,

	@Schema(description = "학생회 당선 인증 사진 url", example = "https://www.example.com.png")
	String electionImageUrl
) {
}
