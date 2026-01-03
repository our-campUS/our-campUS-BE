package com.campus.campus.domain.councilnotice.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

	NOTICE_NOT_FOUND(2501, HttpStatus.NOT_FOUND, "공지를 찾을 수 없습니다."),
	NOT_NOTICE_WRITER(2502, HttpStatus.FORBIDDEN, "작성자만 해당 공지를 수정하거나 삭제할 수 있습니다."),

	//OCI 공지 이미지
	NOTICE_OCI_IMAGE_DELETE_FAILED(6101, HttpStatus.INTERNAL_SERVER_ERROR, "OCI 이미지 삭제에 실패했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
