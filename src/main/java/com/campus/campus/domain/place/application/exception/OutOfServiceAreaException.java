package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class OutOfServiceAreaException extends ApplicationException {
	public OutOfServiceAreaException() {
		super(ErrorCode.OUT_OF_SERVICE_AREA);
	}
}
