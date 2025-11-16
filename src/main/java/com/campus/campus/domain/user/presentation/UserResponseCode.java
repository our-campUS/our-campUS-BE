package com.campus.campus.domain.user.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserResponseCode implements ResponseCodeInterface {
	LOGIN_SUCCESS(200, HttpStatus.OK, "로그인에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
