package com.campus.campus.domain.manager.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ManagerLoginResponse(
	@Schema(description = "access token", example = "랜덤 accessToken")
	String accessToken,

	@Schema(description = "refresh token", example = "랜덤 refreshToken")
	String refreshToken,

	@Schema(description = "관리자 ID", example = "1")
	Long managerId,

	@Schema(description = "관리자 이름", example = "한승현")
	String managerName
) {
}
