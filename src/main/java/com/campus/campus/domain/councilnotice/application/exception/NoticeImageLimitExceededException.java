package com.campus.campus.domain.councilnotice.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NoticeImageLimitExceededException extends ApplicationException {
	public NoticeImageLimitExceededException() {
		super(ErrorCode.NOTICE_IMAGE_LIMIT_EXCEEDED);
	}
}
