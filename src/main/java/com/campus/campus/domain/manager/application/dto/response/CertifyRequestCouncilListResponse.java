package com.campus.campus.domain.manager.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record CertifyRequestCouncilListResponse(
	@Schema(description = "인증 요청한 학생회 id", example = "1")
	Long councilId,

	@Schema(description = "인증 요청한 학생회 이름", example = "가천대학교 총학생회")
	String councilName,

	@Schema(description = "학생회 생성시간(요청시간)", example = "2026-01-05T11:18:52.92955")
	LocalDateTime createdAt
) {
}
