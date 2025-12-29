package com.campus.campus.domain.mail.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class InvalidSchoolEmailException extends ApplicationException {
	public InvalidSchoolEmailException() {
		super(ErrorCode.INVALID_SCHOOL_EMAIL);
	}
}
