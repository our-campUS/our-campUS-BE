package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class CouncilSignupForbiddenException extends ApplicationException {
	public CouncilSignupForbiddenException() {
		super(ErrorCode.COUNCIL_SIGNUP_FORBIDDEN_NOW);
	}
}
