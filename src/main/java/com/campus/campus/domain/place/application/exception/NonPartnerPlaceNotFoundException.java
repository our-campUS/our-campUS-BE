package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NonPartnerPlaceNotFoundException extends ApplicationException {
	public NonPartnerPlaceNotFoundException() {
		super(ErrorCode.PLACE_NOT_FOUND);
	}
}
