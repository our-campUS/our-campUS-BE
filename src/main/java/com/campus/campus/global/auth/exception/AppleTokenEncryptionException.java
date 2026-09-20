package com.campus.campus.global.auth.exception;

import com.campus.campus.global.common.exception.ApplicationException;

public class AppleTokenEncryptionException extends ApplicationException {
	public AppleTokenEncryptionException() {
		super(ErrorCode.APPLE_TOKEN_CRYPTO_FAILED);
	}

	public AppleTokenEncryptionException(Throwable cause) {
		super(ErrorCode.APPLE_TOKEN_CRYPTO_FAILED);
		initCause(cause);
	}
}
