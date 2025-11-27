package com.campus.campus.domain.school.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class CollegeNotFoundException extends ApplicationException {
	public CollegeNotFoundException() {
		super(ErrorCode.COLLEGE_NOT_FOUND);
	}
}
