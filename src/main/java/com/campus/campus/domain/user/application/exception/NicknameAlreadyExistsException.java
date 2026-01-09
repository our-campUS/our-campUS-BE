package com.campus.campus.domain.user.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NicknameAlreadyExistsException extends ApplicationException {
	public NicknameAlreadyExistsException() {
		super(ErrorCode.NICKNAME_ALREADY_EXISTS);
	}
}
