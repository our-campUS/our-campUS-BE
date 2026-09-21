package com.campus.campus.global.auth.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class AppleTokenExchangeException extends ApplicationException {

	public AppleTokenExchangeException() {
		super(ErrorCode.APPLE_TOKEN_EXCHANGE_FAILED);
	}
}
