package com.campus.campus.global.firebase.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public record FcmMessageRequestDto(

	@Schema(description = "유저ID", example = "1")
	Long userId,

	@Schema(description = "메시지 발송인", example = "중앙대학교 학생회")
	String title,

	@Schema(description = "메시지 내용", example = "새로운 제휴가 등록되었습니다.")
	String body
) {
}
