package com.campus.campus.domain.council.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class StudentCouncilNotFoundException extends ApplicationException {
	public StudentCouncilNotFoundException() {
		super(ErrorCode.COUNCIL_NOT_FOUND);
	}
}
