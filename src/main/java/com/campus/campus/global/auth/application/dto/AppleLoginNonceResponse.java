package com.campus.campus.global.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AppleLoginNonceResponse(
	@Schema(description = "Apple 로그인 요청과 ID Token을 연결하는 일회성 nonce")
	String nonce
) {
}
