package com.campus.campus.domain.manager.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CouncilApproveOrDenyResponse(
	@Schema(description = "인증한 학생회 id", example = "1")
	Long councilId,

	@Schema(description = "인증 결과", example = "true")
	boolean certifyResult
) {
}
