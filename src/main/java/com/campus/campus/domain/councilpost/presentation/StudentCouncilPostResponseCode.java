package com.campus.campus.domain.councilpost.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StudentCouncilPostResponseCode implements ResponseCodeInterface {

	POST_CREATE_SUCCESS(201, HttpStatus.CREATED, "게시글 생성에 성공했습니다."),
	POST_READ_SUCCESS(200, HttpStatus.OK, "게시글 조회에 성공했습니다."),
	POST_LIST_READ_SUCCESS(200, HttpStatus.OK, "게시글 목록 조회에 성공했습니다."),
	POST_UPDATE_SUCCESS(200, HttpStatus.OK, "게시글 수정에 성공했습니다."),
	POST_DELETE_SUCCESS(200, HttpStatus.OK, "게시글 삭제에 성공했습니다."),

	UPCOMING_SCHOOL_EVENT_LIST_READ_SUCCESS(200, HttpStatus.OK, "학교 72시간 이내 행사 조회에 성공했습니다."),
	UPCOMING_COLLEGE_EVENT_LIST_READ_SUCCESS(200, HttpStatus.OK, "단과대 72시간 이내 행사 조회에 성공했습니다."),
	UPCOMING_MAJOR_EVENT_LIST_READ_SUCCESS(200, HttpStatus.OK, "학과 72시간 이내 행사 조회에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
