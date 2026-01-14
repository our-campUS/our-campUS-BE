package com.campus.campus.domain.notification.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationResponseCode implements ResponseCodeInterface {

	NOTIFICATION_LIST_READ_SUCCESS(200, HttpStatus.OK, "알림 목록 조회 성공"),
	UNREAD_COUNT_READ_SUCCESS(200, HttpStatus.OK, "읽지 않은 알림 개수 조회 성공"),
	NOTIFICATION_READ_SUCCESS(200, HttpStatus.OK, "알림 읽음 처리 성공"),
	ALL_NOTIFICATIONS_READ_SUCCESS(200, HttpStatus.OK, "모든 알림 읽음 처리 성공"),
	NOTIFICATIONS_READ_SUCCESS(200, HttpStatus.OK, "선택 알림 읽음 처리 성공"),
	NOTIFICATION_UNREAD_EXISTS_SUCCESS(200, HttpStatus.OK, "미확인 알림 존재 여부 조회에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
