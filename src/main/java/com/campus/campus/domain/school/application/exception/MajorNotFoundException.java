package com.campus.campus.domain.school.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class MajorNotFoundException extends ApplicationException {
	public MajorNotFoundException() {
		super(ErrorCode.MAJOR_NOT_FOUND_EXCEPTION);
	}
}
