package com.campus.campus.domain.user.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class SignupForbiddenException extends ApplicationException {
	public SignupForbiddenException() {
		super(ErrorCode.SIGNUP_FORBIDDEN_NOW);
	}
}
