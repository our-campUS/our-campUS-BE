package com.campus.campus.domain.studentcouncilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class PostNotFoundException extends ApplicationException {
	public PostNotFoundException() {
		super(ErrorCode.POST_NOT_FOUND);
	}
}

