package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class PasswordNotCorrectException extends ApplicationException {
	public PasswordNotCorrectException() {
		super(ErrorCode.PASSWORD_NOT_CORRECT);
	}
}
