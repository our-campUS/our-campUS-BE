package com.campus.campus.domain.mail.application.exception;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
	EMAIL_VERIFICATION_NOT_FOUND(2400, HttpStatus.NOT_FOUND, "해당 이메일 인증 정보가 존재하지 않습니다."),
	VERIFICATION_CODE_EXPIRED(2401, HttpStatus.REQUEST_TIMEOUT, "인증 코드가 만료되었습니다."),
	VERIFICATION_CODE_NOT_MATCH(2402, HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),
	EMAIL_NOT_VERIFIED(2403, HttpStatus.UNAUTHORIZED, "이메일 인증이 되지 않았습니다."),
	INVALID_SCHOOL_EMAIL(2404, HttpStatus.BAD_REQUEST, "학교 이메일(.ac.kr 혹은 .edu)만 인증 가능합니다."),
	VERIFICATION_INVALID(2405, HttpStatus.BAD_REQUEST, "유효하지 않은 인증 정보입니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
