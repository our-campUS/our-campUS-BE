package com.campus.campus.domain.user.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserResponseCode implements ResponseCodeInterface {
	LOGIN_SUCCESS(200, HttpStatus.OK, "로그인에 성공했습니다."),
	FIRST_PROFILE_WRITE(200, HttpStatus.OK, " 프로필(학교 정보) 입력에 성공했습니다."),
	NICKNAME_UPDATE_SUCCESS(200, HttpStatus.OK, "닉네임 변경에 성공했습니다."),
	WITHDRAW_SUCCESS(200, HttpStatus.OK, "회원탈퇴에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
