package com.campus.campus.domain.councilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class PostAccessDeniedException extends ApplicationException {
	public PostAccessDeniedException() {
		super(ErrorCode.POST_ACCESS_DENIED);
	}
}
