package com.campus.campus.domain.manager.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CouncilApproveOrDenyRequest(
	@NotNull
	@Schema(description = "인증 결과", example = "true")
	boolean certifyResult
) {
}
