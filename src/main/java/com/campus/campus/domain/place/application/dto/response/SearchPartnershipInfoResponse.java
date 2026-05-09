package com.campus.campus.domain.place.application.dto.response;

import com.campus.campus.domain.council.domain.entity.CouncilType;

import io.swagger.v3.oas.annotations.media.Schema;

public record SearchPartnershipInfoResponse(
	@Schema(description = "제휴 게시글 ID", example = "10")
	Long postId,

	@Schema(description = "학생회 이름", example = "가천대학교 총학생회")
	String councilName,

	@Schema(description = "학생회 타입", example = "가천대학교 총학생회")
	CouncilType councilType,

	@Schema(description = "제휴 제목", example = "전 메뉴 10% 할인")
	String partnershipTitle
) {
}
