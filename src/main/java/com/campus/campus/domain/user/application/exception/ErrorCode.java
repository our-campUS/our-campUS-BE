package com.campus.campus.domain.user.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	USER_NOT_FOUND(2100, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
	USER_NOT_FIRST_LOGIN(2101, HttpStatus.BAD_REQUEST, "최초 로그인한 사용자가 아닙니다."),
	NICKNAME_NOT_MATCH(2102, HttpStatus.BAD_REQUEST, "닉네임이 일치하지 않습니다."),
	USER_SIGNUP_FORBIDDEN_NOW(2103, HttpStatus.FORBIDDEN, "현재 회원가입할 수 없는 계정입니다."),
	NICKNAME_ALREADY_EXISTS(2104, HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
	ACADEMIC_INFO_CANNOT_CHANGE(2105, HttpStatus.BAD_REQUEST, "학적 정보는 6개월에 한번만 변경 가능합니다."),
	CAU_SCHOOL_ONLY(2106, HttpStatus.BAD_REQUEST, "중앙대학교 학생만 가입할 수 있습니다."),
	INQUIRY_NOT_FOUND(2107, HttpStatus.NOT_FOUND, "존재하지 않는 문의입니다."),
	ALREADY_ANSWERED_INQUIRY(2108, HttpStatus.BAD_REQUEST, "이미 답변이 완료된 문의입니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
