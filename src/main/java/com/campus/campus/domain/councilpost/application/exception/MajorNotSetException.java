package com.campus.campus.domain.councilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class MajorNotSetException extends ApplicationException {
	public MajorNotSetException() {
		super(ErrorCode.MAJOR_NOT_SET);
	}
}
