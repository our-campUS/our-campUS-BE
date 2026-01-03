package com.campus.campus.domain.councilnotice.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NoticeNotFoundException extends ApplicationException {
	public NoticeNotFoundException() {
		super(ErrorCode.NOTICE_NOT_FOUND);
	}
}
