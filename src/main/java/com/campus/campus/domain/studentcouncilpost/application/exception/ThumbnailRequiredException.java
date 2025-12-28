package com.campus.campus.domain.studentcouncilpost.application.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class ThumbnailRequiredException extends ApplicationException {
	public ThumbnailRequiredException() {
		super(ErrorCode.THUMBNAIL_REQUIRED);
	}
}
