package com.campus.campus.domain.manager.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ManagerResponseCode implements ResponseCodeInterface {
	MANAGER_LOGIN_SUCCESS(200, HttpStatus.OK, "관리자 로그인에 성공했습니다."),
	COUNCIL_APPROVE_OR_DENY_SUCCESS(200, HttpStatus.OK, "학생회 승인 혹은 거부 및 메일 전송에 성공했습니다."),
	CERTIFY_REQUEST_LIST_SUCCESS(200, HttpStatus.OK, "학생회 인증 요청 목록 조회에 성공했습니다."),
	CERTIFY_REQUEST_ELECTION_IMAGE_SUCCESS(200, HttpStatus.OK, "해당 학생회 인증 요청 당선 사진 조회에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
