package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class LoginIdAlreadyExistsException extends ApplicationException {
	public LoginIdAlreadyExistsException() {
		super(ErrorCode.LOGIN_ID_ALREADY_EXISTS);
	}
}
