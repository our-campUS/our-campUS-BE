package com.campus.campus.domain.studentCouncilNotice.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeResponseCode implements ResponseCodeInterface {

	NOTICE_CREATE_SUCCESS(201, HttpStatus.CREATED, "공지 생성에 성공했습니다."),
	NOTICE_READ_SUCCESS(200, HttpStatus.OK, "공지 조회에 성공했습니다."),
	NOTICE_LIST_READ_SUCCESS(200, HttpStatus.OK, "공지 목록 조회에 성공했습니다."),
	NOTICE_UPDATE_SUCCESS(200, HttpStatus.OK, "공지 수정에 성공했습니다."),
	NOTICE_DELETE_SUCCESS(204, HttpStatus.NO_CONTENT, "공지 삭제에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}

