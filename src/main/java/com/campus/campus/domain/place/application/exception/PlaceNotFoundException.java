package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class PlaceNotFoundException extends ApplicationException {
	public PlaceNotFoundException() {
		super(ErrorCode.PLACE_NOT_FOUND);
	}
}
