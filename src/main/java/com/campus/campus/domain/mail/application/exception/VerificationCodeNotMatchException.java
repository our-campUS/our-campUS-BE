package com.campus.campus.domain.mail.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class VerificationCodeNotMatchException extends ApplicationException {
	public VerificationCodeNotMatchException() {
		super(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND);
	}
}
