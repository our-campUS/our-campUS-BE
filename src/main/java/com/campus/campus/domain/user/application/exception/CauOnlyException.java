package com.campus.campus.domain.user.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class CauOnlyException extends ApplicationException {
	public CauOnlyException() {
		super(ErrorCode.CAU_SCHOOL_ONLY);
	}
}
