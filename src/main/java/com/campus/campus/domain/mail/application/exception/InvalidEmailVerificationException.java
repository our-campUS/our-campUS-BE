package com.campus.campus.domain.mail.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class InvalidEmailVerificationException extends ApplicationException {
	public InvalidEmailVerificationException() {
		super(ErrorCode.VERIFICATION_INVALID);
	}
}
