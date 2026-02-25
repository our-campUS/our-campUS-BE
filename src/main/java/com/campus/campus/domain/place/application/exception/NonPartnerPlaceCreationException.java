package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NonPartnerPlaceCreationException extends ApplicationException {
	public NonPartnerPlaceCreationException() {
		super(ErrorCode.PLACE_CREATION_ERROR);
	}
}
