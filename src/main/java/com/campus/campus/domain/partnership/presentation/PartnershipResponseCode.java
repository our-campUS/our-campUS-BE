package com.campus.campus.domain.partnership.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartnershipResponseCode implements ResponseCodeInterface {

	PLACE_SAVE_SUCCESS(200, HttpStatus.OK, "좋아요 처리가 완료되었습니다."),
	CHECK_PARTNERSHIP_PLACES_SUCCESS(200, HttpStatus.OK, "제휴 장소 리스트 조회가 완료되었습니다."),
	CHECK_ONE_PARTNERSHIP_PLACE_SUCCESS(200, HttpStatus.OK, "제휴 장소 단건 조회가 완료되었습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
