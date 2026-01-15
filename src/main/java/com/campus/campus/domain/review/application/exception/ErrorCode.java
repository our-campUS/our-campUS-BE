package com.campus.campus.domain.review.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

	REVIEW_NOT_FOUND(2700, HttpStatus.NOT_FOUND, "리뷰글을 찾을 수 없습니다."),
	NOT_REVIEW_WRITER(2701, HttpStatus.FORBIDDEN, "작성자만 해당 작업을 수행할 수 있습니다."),
	RECEIPT_OCR_FAILED(2702, HttpStatus.UNPROCESSABLE_ENTITY, "OCR 인식에 실패하였습니다."),
	RECEIPT_FILE_CONVERT_ERROR(2703, HttpStatus.UNPROCESSABLE_ENTITY, "영수증 FILE 형태 변형에 실패하였습니다."),
	RECEIPT_FILE_TYPE_ERROR(2704, HttpStatus.UNPROCESSABLE_ENTITY, "지원하지 않는 이미지 형식입니다."),
	NOT_PARTNERSHIP_RECEIPT_ERROR(2705, HttpStatus.UNPROCESSABLE_ENTITY, "영수증과 일치하는 제휴 정보를 찾을 수 없어요.");


	private final int code;
	private final HttpStatus status;
	private final String message;

}
