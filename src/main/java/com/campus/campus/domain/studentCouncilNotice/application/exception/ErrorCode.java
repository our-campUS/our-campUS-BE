package com.campus.campus.domain.studentCouncilNotice.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

	NOTICE_NOT_FOUND(2501, HttpStatus.NOT_FOUND, "공지를 찾을 수 없습니다."),
	NOT_NOTICE_WRITER(2502, HttpStatus.FORBIDDEN, "작성자만 해당 공지를 수정하거나 삭제할 수 있습니다."),
	NOTICE_IMAGE_LIMIT_EXCEEDED(2503, HttpStatus.BAD_REQUEST, "공지 이미지는 최대 10개까지 등록할 수 있습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}

