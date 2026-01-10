package com.campus.campus.domain.councilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class AcademicInfoNotSetException extends ApplicationException {
	public AcademicInfoNotSetException() {
		super(ErrorCode.ACADEMIC_INFO_NOT_SET);
	}
}
