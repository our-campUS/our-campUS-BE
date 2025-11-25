package com.campus.campus.domain.mail.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class VerificationCodeExpiredException extends ApplicationException {
	public VerificationCodeExpiredException() {
		super(ErrorCode.VERIFICATION_CODE_NOT_MATCH);
	}
}
