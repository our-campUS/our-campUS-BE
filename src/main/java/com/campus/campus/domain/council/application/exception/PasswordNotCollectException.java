package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class PasswordNotCollectException extends ApplicationException {
	public PasswordNotCollectException() {
		super(ErrorCode.PASSWORD_NOT_COLLECT);
	}
}
