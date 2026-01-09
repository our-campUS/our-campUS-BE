package com.campus.campus.global.firebase.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

	FIREBASE_INITIALIZATION_FAILED(6201, HttpStatus.INTERNAL_SERVER_ERROR, "Firebase 초기화에 실패했습니다."),
	FCM_TOPIC_SEND_FAILED(6202, HttpStatus.INTERNAL_SERVER_ERROR, "푸시 알림 전송에 실패했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
