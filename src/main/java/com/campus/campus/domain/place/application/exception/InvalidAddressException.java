package com.campus.campus.domain.place.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class InvalidAddressException extends ApplicationException {
	public InvalidAddressException() {
		super(ErrorCode.ADDRESS_EMPTY);
	}
}
