package com.campus.campus.domain.place.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	ADDRESS_EMPTY(2600, HttpStatus.NO_CONTENT, "주소는 비어있을 수 없습니다."),
	COORDINATE_NOT_FOUND(2601, HttpStatus.NOT_FOUND, "좌표를 찾을 수 없습니다."),
	PLACE_NOT_FOUND(2602, HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다."),
	SHA256_NOT_SUPPORTED(2603, HttpStatus.INTERNAL_SERVER_ERROR, "SHA-256이 지원되지 않습니다."),
	NAVER_API_ERROR(2604, HttpStatus.INTERNAL_SERVER_ERROR, "네이버 api 호출에 실패하였습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}