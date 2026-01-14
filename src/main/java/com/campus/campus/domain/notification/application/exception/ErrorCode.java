package com.campus.campus.domain.notification.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

	NOTIFICATION_NOT_FOUND(2801, HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),
	NOTIFICATION_ACCESS_DENIED(2802, HttpStatus.FORBIDDEN, "해당 알림에 접근할 권한이 없습니다."),
	INVALID_NOTIFICATION_IDS(2803, HttpStatus.BAD_REQUEST, "유효하지 않은 알림 ID 목록입니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
