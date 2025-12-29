package com.campus.campus.domain.councilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NotPostWriterException extends ApplicationException {
	public NotPostWriterException() {
		super(ErrorCode.NOT_POST_WRITER);
	}
}
