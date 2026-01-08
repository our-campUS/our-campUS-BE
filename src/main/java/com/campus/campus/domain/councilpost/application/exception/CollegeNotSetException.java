package com.campus.campus.domain.councilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class CollegeNotSetException extends ApplicationException {
	public CollegeNotSetException() {
		super(ErrorCode.COLLEGE_NOT_SET);
	}
}
