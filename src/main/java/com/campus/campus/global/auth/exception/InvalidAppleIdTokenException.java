package com.campus.campus.global.auth.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class InvalidAppleIdTokenException extends ApplicationException {

	public InvalidAppleIdTokenException() {
		super(ErrorCode.APPLE_ID_TOKEN_INVALID);
	}
}
