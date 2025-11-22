package com.campus.campus.global.util.jwt.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class InvalidJwtException extends ApplicationException {
	public InvalidJwtException() {
		super(ErrorCode.JWT_INVALID);
	}
}
