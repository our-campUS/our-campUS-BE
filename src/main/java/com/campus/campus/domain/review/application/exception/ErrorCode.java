package com.campus.campus.domain.review.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

	REVIEW_NOT_FOUND(2700, HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
	NOT_REVIEW_WRITER(2701, HttpStatus.FORBIDDEN, "작성자만 해당 작업을 수행할 수 있습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
