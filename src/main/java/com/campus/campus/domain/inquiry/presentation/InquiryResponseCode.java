package com.campus.campus.domain.inquiry.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryResponseCode implements ResponseCodeInterface {

	INQUIRY_CREATE_SUCCESS(201, HttpStatus.CREATED, "문의 생성에 성공했습니다."),
	INQUIRY_READ_SUCCESS(200, HttpStatus.OK, "문의 내역 조회에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
