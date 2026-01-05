package com.campus.campus.domain.userpost.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

	POST_ACCESS_DENIED(2201, HttpStatus.FORBIDDEN, "해당 게시글에 접근할 권한이 없습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
