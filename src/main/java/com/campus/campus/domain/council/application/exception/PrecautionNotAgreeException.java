package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class PrecautionNotAgreeException extends ApplicationException {
	public PrecautionNotAgreeException() {
		super(ErrorCode.PRECAUTION_NOT_AGREE);
	}
}
