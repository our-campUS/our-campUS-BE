package com.campus.campus.domain.councilpost.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetActivePartnershipListForUserResponse(
	@Schema(description = "제휴글 id", example = "1")
	Long postId,

	@Schema(description = "제휴글 이름", example = "투썸 제휴")
	String title,

	@Schema(description = "제휴 장소", example = "투썸 플레이스")
	String place,

	@Schema(description = "썸네일 image url", example = "https://www.example.com.png")
	String thumbnailImageUrl
) {
}
