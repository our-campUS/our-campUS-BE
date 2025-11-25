package com.campus.campus.domain.mail.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailVerificationResponseCode implements ResponseCodeInterface {
	EMAIL_SEND_SUCCESS(200, HttpStatus.OK, "성공적으로 이메일이 전송되었습니다."),
	VERIFY_SUCCESS(200, HttpStatus.OK, "인증에 성공하였습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
