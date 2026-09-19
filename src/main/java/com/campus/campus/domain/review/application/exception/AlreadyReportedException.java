package com.campus.campus.domain.review.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class AlreadyReportedException extends ApplicationException {
	public AlreadyReportedException() {
		super(ErrorCode.ALREADY_REPORTED);
	}
}
