package com.campus.campus.domain.school.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class SchoolCollegeNotSameException extends ApplicationException {
	public SchoolCollegeNotSameException() {
		super(ErrorCode.SCHOOL_COLLEGE_NOT_SAME);
	}
}
