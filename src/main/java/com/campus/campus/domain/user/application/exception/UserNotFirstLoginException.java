package com.campus.campus.domain.user.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class UserNotFirstLoginException extends ApplicationException {
	public UserNotFirstLoginException() {
		super(ErrorCode.USER_NOT_FIRST_LOGIN);
	}
}
