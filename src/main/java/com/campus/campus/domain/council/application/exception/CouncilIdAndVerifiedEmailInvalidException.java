package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class CouncilIdAndVerifiedEmailInvalidException extends ApplicationException {
	public CouncilIdAndVerifiedEmailInvalidException() {
		super(ErrorCode.ID_EMAIL_INVALID);
	}
}
