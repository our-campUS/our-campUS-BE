package com.campus.campus.domain.council.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	COUNCIL_NOT_FOUND(2300, HttpStatus.NOT_FOUND, "해당 학생회가 존재하지 않습니다."),
	LOGIN_ID_ALREADY_EXISTS(2301, HttpStatus.CONFLICT, "이미 가입된 아이디입니다."),
	EMAIL_ALREADY_EXISTS(2302, HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
	INVALID_COUNCIL_SCOPE(2303, HttpStatus.BAD_REQUEST, "학생회 범위에 맞지 않습니다."),
	PASSWORD_NOT_CORRECT(2304, HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),
	SIGNUP_EMAIL_NOT_FOUND(2305, HttpStatus.NOT_FOUND, "해당 이메일로 가입된 아이디가 없습니다."),
	ID_EMAIL_INVALID(2306, HttpStatus.BAD_REQUEST, "아이디에 해당하는 학생회의 이메일이 아닙니다."),
	NEW_PASSWORD_IS_CURRENT_PASSWORD(2307, HttpStatus.BAD_REQUEST, "기존 비밀번호와 새 비밀번호가 동일합니다."),
	NEW_PASSWORD_CONFIRM_NOT_CORRECT(2308, HttpStatus.BAD_REQUEST, "비밀번호 재입력이 일치하지 않습니다."),
	PRECAUTION_NOT_AGREE(2309, HttpStatus.BAD_REQUEST, "유의사항에 동의하지 않았습니다."),
	COUNCIL_SIGNUP_FORBIDDEN_NOW(2310, HttpStatus.FORBIDDEN,"현재 학생회 회원가입할 수 없는 계정입니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
