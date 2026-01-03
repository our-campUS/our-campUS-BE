package com.campus.campus.domain.councilpost.application.exception;

import com.campus.campus.domain.councilnotice.application.exception.ErrorCode;
import com.campus.campus.global.common.exception.ApplicationException;

public class PostOciImageDeleteFailedException extends ApplicationException {
	public PostOciImageDeleteFailedException() {
		super(ErrorCode.NOTICE_OCI_IMAGE_DELETE_FAILED);
	}
}
