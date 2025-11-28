package com.campus.campus.domain.school.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SchoolResponseCode implements ResponseCodeInterface {
	SCHOOL_FIND_SUCCESS(200, HttpStatus.OK, "학교가 성공적으로 검색되었습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
