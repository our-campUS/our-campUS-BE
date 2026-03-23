package com.campus.campus.domain.councilpost.application.dto.request;

import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record CouncilPostCreatedEvent(
	@Schema(description = "게시글 id", example = "1")
	Long postId,

	@Schema(description = "학생회 이름", example = "중앙대학교 총학생회")
	String councilName,

	@Schema(description = "학생회 id", example = "1")
	StudentCouncil studentCouncil,

	@Schema(description = "게시글 카테고리", example = "EVENT")
	PostCategory category,

	@Schema(description = "FCM 구독 토픽", example = "school_1")
	String topic
) {
}
