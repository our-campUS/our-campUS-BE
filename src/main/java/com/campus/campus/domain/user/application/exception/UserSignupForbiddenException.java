package com.campus.campus.domain.user.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class UserSignupForbiddenException extends ApplicationException {
	public UserSignupForbiddenException() {
		super(ErrorCode.USER_SIGNUP_FORBIDDEN_NOW);
	}
}
