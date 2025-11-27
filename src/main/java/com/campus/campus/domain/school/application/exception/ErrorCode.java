package com.campus.campus.domain.school.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	SCHOOL_NOT_FOUND(2200, HttpStatus.NOT_FOUND, "해당 학교는 존재하지 않습니다."),
	MAJOR_NOT_FOUND(2201, HttpStatus.NOT_FOUND, "해당 학과는 존재하지 않습니다."),
	COLLEGE_NOT_FOUND(2202, HttpStatus.NOT_FOUND, "해당 단과대는 존재하지 않습니다."),
	SCHOOL_COLLEGE_NOT_SAME(2203, HttpStatus.BAD_REQUEST, "선택한 학교와 단과대가 일치하지 않습니다."),
	SCHOOL_MAJOR_NOT_SAME(2204, HttpStatus.BAD_REQUEST, "선택한 학교와 전공이 일치하지 않습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
