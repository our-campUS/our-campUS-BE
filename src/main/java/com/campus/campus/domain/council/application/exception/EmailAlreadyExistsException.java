package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class EmailAlreadyExistsException extends ApplicationException {
	public EmailAlreadyExistsException() {
		super(ErrorCode.EMAIL_ALREADY_EXISTS);
	}
}
