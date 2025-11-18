package com.campus.campus.global.util.jwt.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class UnAuthorizedException extends ApplicationException {
	public UnAuthorizedException() {
		super(ErrorCode.UNAUTHORIZED);
	}
}
