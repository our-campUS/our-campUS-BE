package com.campus.campus.global.util.jwt.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class ExpireJwtException extends ApplicationException {
	public ExpireJwtException() {
		super(ErrorCode.JWT_EXPIRED);
	}
}
