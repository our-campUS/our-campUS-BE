package com.campus.campus.domain.mail.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class EmailVerificationNotFoundException extends ApplicationException {
	public EmailVerificationNotFoundException() {
		super(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND);
	}
}
