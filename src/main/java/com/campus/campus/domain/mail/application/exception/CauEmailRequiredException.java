package com.campus.campus.domain.mail.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class CauEmailRequiredException extends ApplicationException {
	public CauEmailRequiredException() {
		super(ErrorCode.CAU_EMAIL_REQUIRED);
	}
}
