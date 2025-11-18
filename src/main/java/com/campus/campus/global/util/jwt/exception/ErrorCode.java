package com.campus.campus.global.util.jwt.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	JWT_INVALID(2001, HttpStatus.NOT_FOUND, "유효하지 않은 JWT 토큰입니다."),
	JWT_EXPIRED(2002, HttpStatus.BAD_REQUEST, "만료된 JWT 토큰입니다."),
	UNAUTHORIZED(2003, HttpStatus.UNAUTHORIZED, "인증되지 않았습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
