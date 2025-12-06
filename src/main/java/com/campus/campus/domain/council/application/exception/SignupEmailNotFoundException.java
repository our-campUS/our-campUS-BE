package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class SignupEmailNotFoundException extends ApplicationException {
	public SignupEmailNotFoundException() {
		super(ErrorCode.SIGNUP_EMAIL_NOT_FOUND);
	}
}
