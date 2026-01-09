package com.campus.campus.global.firebase.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public record FcmMessageRequestDto(

	@Schema(description = "유저ID", example = "1")
	Long userId,

	@Schema(description = "메시지 발송인", example = "시스템")
	String title,

	@Schema(description = "메시지 내용", example = "이제 서연님에게 화이팅을 할 수 있어요.")
	String body
) {
}
