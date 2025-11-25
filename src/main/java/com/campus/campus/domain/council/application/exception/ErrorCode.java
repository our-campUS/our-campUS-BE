package com.campus.campus.domain.council.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	COUNCIL_NOT_FOUND(2300, HttpStatus.NOT_FOUND, "해당 학생회가 존재하지 않습니다."),
	LOGIN_ID_ALREADY_EXISTS(2301, HttpStatus.CONFLICT, "이미 가입된 아이디입니다."),
	EMAIL_ALREADY_EXISTS(2302, HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
	INVALID_COUNCIL_SCOPE(2302, HttpStatus.BAD_REQUEST, "학생회 범위에 맞지 않습니다."),
	PASSWORD_NOT_COLLECT(2303, HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
