package com.campus.campus.domain.review.presentation;

import org.springframework.http.HttpStatus;

import com.campus.campus.global.common.response.ResponseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewResponseCode implements ResponseCodeInterface {

	REVIEW_SAVE_SUCCESS(200, HttpStatus.OK, "리뷰 작성이 완료되었습니다."),
	REVIEW_DELETE_SUCCESS(200, HttpStatus.OK, "리뷰 삭제가 완료되었습니다."),
	REVIEW_UPDATE_SUCCESS(200, HttpStatus.OK, "리뷰 수정이 완료되었습니다."),
	GET_REVIEW_LIST_SUCCESS(200, HttpStatus.OK, "리뷰 리스트 조회에 성공하였습니다."),
	GET_RANK_SUCCESS(200, HttpStatus.OK, "최근 한달 리뷰 순에 따른 조회 가게 조회에 성공하였습니다."),
	GET_REVIEW_SUCCESS(200, HttpStatus.OK, "리뷰 상세 조회에 성공하였습니다."),
	OCR_SUCCESS(200, HttpStatus.OK, "OCR 인식에 성공하였습니다."),
	GET_MY_REVIEWS_SUCCESS(200, HttpStatus.OK, "내가 쓴 리뷰 조회에 성공했습니다.");

	private final int code;
	private final HttpStatus status;
	private final String message;
}
