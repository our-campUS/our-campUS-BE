package com.campus.campus.domain.user.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class AcademicInfoUpdateRestrictionException extends ApplicationException {
	public AcademicInfoUpdateRestrictionException() {
		super(ErrorCode.ACADEMIC_INFO_CANNOT_CHANGE);
	}
}
