package com.campus.campus.domain.manager.application.exception;

import com.campus.campus.domain.council.application.exception.ErrorCode;
import com.campus.campus.global.common.exception.ApplicationException;

public class PasswordNotCorrectException extends ApplicationException {
	public PasswordNotCorrectException() {
		super(ErrorCode.PASSWORD_NOT_CORRECT);
	}
}
