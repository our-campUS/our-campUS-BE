package com.campus.campus.global.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OcrErrorCode implements ErrorCodeInterface {

    // 5000번대: OCR 에러

    // 5100번대: OCR API 연결/인증 에러
    OCR_CONNECTION_FAILED(5101, HttpStatus.INTERNAL_SERVER_ERROR, "OCR API 연결에 실패했습니다."),
    OCR_TIMEOUT(5102, HttpStatus.GATEWAY_TIMEOUT, "OCR API 요청 시간이 초과되었습니다."),
    OCR_RATE_LIMIT_EXCEEDED(5103, HttpStatus.TOO_MANY_REQUESTS, "OCR API 요청 한도를 초과했습니다."),
    OCR_AUTHENTICATION_FAILED(5104, HttpStatus.UNAUTHORIZED, "OCR API 인증에 실패했습니다."),
    OCR_API_KEY_INVALID(5105, HttpStatus.UNAUTHORIZED, "OCR API 키가 유효하지 않습니다."),

    // 5200번대: OCR 요청 에러 (클라이언트)
    INVALID_IMAGE_FORMAT(5201, HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다."),
    IMAGE_TOO_LARGE(5202, HttpStatus.BAD_REQUEST, "이미지 크기가 너무 큽니다. (최대 10MB)"),
    IMAGE_QUALITY_TOO_LOW(5203, HttpStatus.BAD_REQUEST, "이미지 품질이 너무 낮아 인식할 수 없습니다."),
    IMAGE_EMPTY(5204, HttpStatus.BAD_REQUEST, "이미지가 비어있습니다."),

    // 5300번대: OCR 응답 처리 에러
    OCR_RESPONSE_INVALID(5301, HttpStatus.INTERNAL_SERVER_ERROR, "OCR 응답 형식이 올바르지 않습니다."),
    OCR_NO_TEXT_DETECTED(5302, HttpStatus.BAD_REQUEST, "이미지에서 텍스트를 감지할 수 없습니다."),
    OCR_PROCESSING_FAILED(5303, HttpStatus.INTERNAL_SERVER_ERROR, "OCR 처리 중 오류가 발생했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}