package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class GeoCoderException extends ApplicationException {
	public GeoCoderException() {
		super(ErrorCode.GEOCODER_ERROR);
	}
}
