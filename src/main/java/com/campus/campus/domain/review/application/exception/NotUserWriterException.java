package com.campus.campus.domain.review.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NotUserWriterException extends ApplicationException {
	public NotUserWriterException() {
		super(ErrorCode.NOT_REVIEW_WRITER);
	}
}
