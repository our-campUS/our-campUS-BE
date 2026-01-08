package com.campus.campus.domain.councilpost.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LikePostResponse(
	@Schema(name = "좋아요 누른 사용자 id", example = "1")
	Long userId,

	@Schema(name = "좋아요 누를 게시글의 id", example = "1")
	Long postId,

	@Schema(name = "좋아요 여부", example = "example")
	boolean liked
) {
}
