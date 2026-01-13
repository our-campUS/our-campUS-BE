package com.campus.campus.domain.stamp.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StampResponseCode implements ResponseCodeInterface {
	REWARD_LIST_SUCCESS(200, HttpStatus.OK, "스탬프 보상 목록 조회에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
