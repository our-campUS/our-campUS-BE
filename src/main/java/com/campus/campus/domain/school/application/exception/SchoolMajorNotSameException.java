package com.campus.campus.domain.school.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class SchoolMajorNotSameException extends ApplicationException {
	public SchoolMajorNotSameException() {
		super(ErrorCode.SCHOOL_MAJOR_NOT_SAME);
	}
}
