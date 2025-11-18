package com.campus.campus.domain.school.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class SchoolNotFoundException extends ApplicationException {
	public SchoolNotFoundException() {
		super(ErrorCode.SCHOOL_NOT_FOUND_EXCEPTION);
	}
}
