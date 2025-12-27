package com.campus.campus.global.oci.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PresignedUrlResponseCode implements ResponseCodeInterface {

	PRESIGNED_URL_SUCCESS(200, HttpStatus.OK, "Presigned URL 생성 성공");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
