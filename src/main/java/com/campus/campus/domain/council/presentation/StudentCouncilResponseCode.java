package com.campus.campus.domain.council.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StudentCouncilResponseCode implements ResponseCodeInterface {
	SIGNUP_REQUEST_SUCCESS(200, HttpStatus.OK, "학생회 회원가입 요청에 성공했습니다."),
	VALIDATE_LOGIN_ID_SUCCESS(200, HttpStatus.OK, "로그인 id 검증에 성공했습니다."),
	LOGIN_SUCCESS(200, HttpStatus.OK, "학생회 로그인에 성공했습니다."),
	FIND_ID_SUCCESS(200, HttpStatus.OK, "아이디 찾기에 성공했습니다."),
	VALIDATE_EMAIL_SUCCESS(200, HttpStatus.OK, "이메일 검증에 성공했습니다."),
	FIND_PASSWORD_SUCCESS(200, HttpStatus.OK, "비밀번호 재설정에 성공했습니다."),
	CHANGE_EMAIL_SUCCESS(200, HttpStatus.OK, "이메일 변경에 성공했습니다."),
	CHANGE_PASSWORD_SUCCESS(200, HttpStatus.OK, "비밀번호 변경에 성공했습니다."),
	COUNCIL_WITHDRAW_SUCCESS(200, HttpStatus.OK, "학생회 회원탈퇴에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
