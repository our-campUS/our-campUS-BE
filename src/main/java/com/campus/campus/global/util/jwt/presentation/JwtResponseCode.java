package com.campus.campus.global.util.jwt.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtResponseCode implements ResponseCodeInterface {
	LOGOUT_SUCCESS(200, HttpStatus.OK, "로그아웃에 성공했습니다."),
	TOKEN_REISSUE_SUCCESS(200, HttpStatus.OK, "토큰 갱신에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
