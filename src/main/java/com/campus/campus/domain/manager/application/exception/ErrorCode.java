package com.campus.campus.domain.manager.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	MANAGER_NOT_FOUND(2701, HttpStatus.NOT_FOUND, "해당 관리자는 존재하지 않습니다."),
	PASSWORD_NOT_CORRECT(2702, HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
