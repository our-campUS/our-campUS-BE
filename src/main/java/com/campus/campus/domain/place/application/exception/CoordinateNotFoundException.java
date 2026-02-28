package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class CoordinateNotFoundException extends ApplicationException {
	public CoordinateNotFoundException() {
		super(ErrorCode.COORDINATE_NOT_FOUND);
	}
}
