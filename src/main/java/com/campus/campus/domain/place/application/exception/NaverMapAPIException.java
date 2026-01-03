package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NaverMapAPIException extends ApplicationException {
	public NaverMapAPIException() {
		super(ErrorCode.NAVER_API_ERROR);
	}
}
