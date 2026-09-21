package com.campus.campus.global.auth.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class AppleClientSecretGenerationException extends ApplicationException {

	public AppleClientSecretGenerationException() {
		super(ErrorCode.APPLE_CLIENT_SECRET_GENERATION_FAILED);
	}
}
