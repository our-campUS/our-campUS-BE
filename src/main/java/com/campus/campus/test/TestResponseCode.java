package com.campus.campus.test;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestResponseCode implements ResponseCodeInterface {
	TEST_SUCCESS(201, HttpStatus.OK, "테스트에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
