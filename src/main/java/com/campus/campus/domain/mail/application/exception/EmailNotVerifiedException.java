package com.campus.campus.domain.mail.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class EmailNotVerifiedException extends ApplicationException {
	public EmailNotVerifiedException() {
		super(ErrorCode.EMAIL_NOT_VERIFIED);
	}
}
