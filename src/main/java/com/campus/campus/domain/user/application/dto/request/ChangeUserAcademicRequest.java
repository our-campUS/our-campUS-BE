package com.campus.campus.domain.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ChangeUserAcademicRequest(
	@Schema(description = "변경할 학교 ID", example = "1")
	@NotNull
	Long schoolId,

	@Schema(description = "변경할 학과 ID", example = "15")
	@NotNull
	Long majorId
) {
}
