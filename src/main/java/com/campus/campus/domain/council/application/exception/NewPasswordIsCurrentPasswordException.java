package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NewPasswordIsCurrentPasswordException extends ApplicationException {
	public NewPasswordIsCurrentPasswordException() {
		super(ErrorCode.NEW_PASSWORD_IS_CURRENT_PASSWORD);
	}
}
