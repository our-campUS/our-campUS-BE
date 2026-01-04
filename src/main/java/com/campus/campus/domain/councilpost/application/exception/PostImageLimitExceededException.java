package com.campus.campus.domain.councilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class PostImageLimitExceededException extends ApplicationException {
	public PostImageLimitExceededException() {
		super(ErrorCode.POST_IMAGE_LIMIT_EXCEEDED);
	}
}
