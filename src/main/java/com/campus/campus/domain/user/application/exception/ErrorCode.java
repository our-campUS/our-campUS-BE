package com.campus.campus.domain.user.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	USER_NOT_FOUND(2100, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
	USER_NOT_FIRST_LOGIN(2101,HttpStatus.BAD_REQUEST, "최초 로그인한 사용가 아닙니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
