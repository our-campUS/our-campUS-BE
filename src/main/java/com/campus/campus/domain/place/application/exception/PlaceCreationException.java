package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class PlaceCreationException extends ApplicationException {
	public PlaceCreationException() {
		super(ErrorCode.PLACE_CREATION_ERROR);
	}
}
