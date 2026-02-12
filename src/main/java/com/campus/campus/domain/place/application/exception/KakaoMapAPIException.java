package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class KakaoMapAPIException extends ApplicationException {
	public KakaoMapAPIException() {
		super(ErrorCode.KAKAO_API_ERROR);
	}
}
