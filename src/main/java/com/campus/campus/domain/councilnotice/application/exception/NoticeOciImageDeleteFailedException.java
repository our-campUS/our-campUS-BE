package com.campus.campus.domain.councilnotice.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class NoticeOciImageDeleteFailedException extends ApplicationException {
	public NoticeOciImageDeleteFailedException() {
		super(ErrorCode.NOTICE_OCI_IMAGE_DELETE_FAILED);
	}
}
