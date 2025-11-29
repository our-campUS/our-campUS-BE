package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class InvalidCouncilScopeException extends ApplicationException {
	public InvalidCouncilScopeException() {
		super(ErrorCode.INVALID_COUNCIL_SCOPE);
	}
}
