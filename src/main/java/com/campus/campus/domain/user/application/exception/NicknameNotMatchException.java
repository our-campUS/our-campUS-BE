package com.campus.campus.domain.user.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NicknameNotMatchException extends ApplicationException {
	public NicknameNotMatchException() {
		super(ErrorCode.NICKNAME_NOT_MATCH);
	}
}
