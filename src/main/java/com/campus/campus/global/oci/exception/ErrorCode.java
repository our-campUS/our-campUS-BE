package com.campus.campus.global.oci.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

	OCI_PRESIGNED_URL_CREATE_FAILED(6100, HttpStatus.INTERNAL_SERVER_ERROR, "Presigned URL 생성에 실패했습니다."),
	OCI_OBJECT_DELETE_FAILED(6101, HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제 중 오류가 발생했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
