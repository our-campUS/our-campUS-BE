package com.campus.campus.global.auth.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	APPLE_ID_TOKEN_INVALID(2004, HttpStatus.UNAUTHORIZED, "유효하지 않은 Apple ID Token입니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
