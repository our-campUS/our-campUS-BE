package com.campus.campus.domain.school.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	SCHOOL_NOT_FOUND_EXCEPTION(2200, HttpStatus.NOT_FOUND, "해당 학교는 존재하지 않습니다."),
	MAJOR_NOT_FOUND_EXCEPTION(2201, HttpStatus.NOT_FOUND, "해당 학과는 존재하지 않습니다."),
	SCHOOL_MAJOR_NOT_SAME(2202, HttpStatus.BAD_REQUEST, "선택한 학교와 전공이 일치하지 않습니다."),

	// ----- OCR-----
	SCHOOL_OCR_CALL_FAILED(6000, HttpStatus.INTERNAL_SERVER_ERROR, "OCR 서버 호출에 실패했습니다."),
	SCHOOL_OCR_PARSE_FAILED(6001, HttpStatus.BAD_REQUEST, "OCR 데이터 파싱에 실패했습니다."),
	SCHOOL_OCR_NO_TEXT(6002, HttpStatus.BAD_REQUEST, "OCR에서 텍스트를 감지하지 못했습니다."),
	SCHOOL_OCR_INVALID_IMAGE(6003, HttpStatus.BAD_REQUEST, "유효하지 않은 이미지입니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
