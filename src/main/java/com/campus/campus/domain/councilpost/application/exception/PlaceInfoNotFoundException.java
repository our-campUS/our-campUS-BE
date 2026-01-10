package com.campus.campus.domain.councilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class PlaceInfoNotFoundException extends ApplicationException {
	public PlaceInfoNotFoundException() {
		super(ErrorCode.PLACE_INFO_NOT_FOUND);
	}
}
