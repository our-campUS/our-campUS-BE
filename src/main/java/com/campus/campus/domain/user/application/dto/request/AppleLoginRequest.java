package com.campus.campus.domain.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AppleLoginRequest(
	@Schema(description = "Apple에서 발급받은 일회성 인증 코드")
	@NotBlank(message = "Apple authorization code는 필수입니다.")
	String authorizationCode,

	@Schema(description = "Apple 로그인 요청에 사용한 일회성 nonce")
	@NotBlank(message = "Apple nonce는 필수입니다.")
	String nonce,

	@Schema(description = "Apple 최초 로그인 시 전달되는 사용자 이름", example = "홍길동")
	String nickname
) {
}
