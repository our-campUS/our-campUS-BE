package com.campus.campus.domain.user.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class UserNotFoundException extends ApplicationException {
	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND);
	}
}
