package com.campus.campus.global.oci.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

	OCI_PRESIGNED_URL_CREATE_FAILED(6100, HttpStatus.INTERNAL_SERVER_ERROR, "Presigned URL 생성에 실패했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
